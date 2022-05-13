package mao.spring_boot_redis_hmdp.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import mao.spring_boot_redis_hmdp.dto.Result;
import mao.spring_boot_redis_hmdp.entity.Shop;
import mao.spring_boot_redis_hmdp.mapper.ShopMapper;
import mao.spring_boot_redis_hmdp.service.IShopService;
import mao.spring_boot_redis_hmdp.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService
{

    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Override
    public Result queryShopById(Long id)
    {
        //获取redisKey
        String redisKey = RedisConstants.CACHE_SHOP_KEY + id;
        //从redis中查询商户信息，根据id
        String shopJson = stringRedisTemplate.opsForValue().get(redisKey);
        //判断取出的数据是否为空
        if (StrUtil.isNotBlank(shopJson))
        {
            //不是空，redis里有，返回
            return Result.ok(JSONUtil.toBean(shopJson, Shop.class));
        }
        //空，查数据库
        Shop shop = this.getById(id);
        //判断数据库里的信息是否为空
        if (shop == null)
        {
            //空，返回错误
            return Result.fail("店铺信息不存在");
        }
        //存在，回写到redis里
        stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(shop));
        //返回数据
        return Result.ok(shop);
    }
}
