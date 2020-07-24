package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.config.UserConfig;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@EnableConfigurationProperties
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private UserConfig props;

    private static final String key_prefix = "user:registry:phone";

    /**
     * 注册判断电话与用户名的唯一性
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        int count = 0;
        User user = new User();
        if(type.equals(1)){
            user.setUsername(data);
             count = userMapper.selectCount(user);
        }else if(type.equals(2)) {
            user.setPhone(data);
             count = userMapper.selectCount(user);
        }else {
            throw new LyException(ExceptionEnum.INVAILD_USER_PARAM_TYPE);
        }
        return count == 0;
    }

    /**
     * 发送短信
     * @param phone
     */
    public void sendCode(String phone) {
        //生成key
        String key = key_prefix+phone;
        //生成验证码(6位随机数)
        String code = NumberUtils.generateCode(6); //TODO
        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("name",code);
        //保存验证码
        //发送验证码
        amqpTemplate.convertAndSend(props.getExchange(),props.getRoutingKey(),msg);
        redisTemplate.opsForValue().set(key,code,props.getLive_time(), TimeUnit.MINUTES);
    }

    public void register(User user, String code) {
        //校验验证码
        if(!StringUtils.equals(code,redisTemplate.opsForValue().get(key_prefix+user.getPhone()))){
            throw new LyException(ExceptionEnum.INVALID_CODE);
        };
        //生成salt
        String salt = CodeUtils.generateSalt();
        user.setSalt(salt);
        //根据salt加密
        user.setPassword(CodeUtils.md5Hex(user.getPassword(),salt));
        //新增用户
        user.setCreated(new Date());
        redisTemplate.delete(key_prefix+user.getPhone());
        userMapper.insert(user);
    }

    public User queryUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        //1根据用户名查找用户
        User recode = userMapper.selectOne(user);
        if (recode == null) {
            return recode;
        }
        //给上传的密码进行加盐加密，
        password = CodeUtils.md5Hex(password,recode.getSalt());
        //与查询到密码进行比较
        if (!StringUtils.equals(password,recode.getPassword())) {
            return null;
        }
        return recode;
    }
}
