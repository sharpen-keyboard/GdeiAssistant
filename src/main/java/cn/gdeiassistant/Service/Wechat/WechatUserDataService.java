package cn.gdeiassistant.Service.Wechat;

import cn.gdeiassistant.Repository.SQL.Mysql.Mapper.GdeiAssistant.User.UserMapper;
import cn.gdeiassistant.Repository.SQL.Mysql.Mapper.GdeiAssistant.WechatUser.WechatUserMapper;
import cn.gdeiassistant.Pojo.Entity.User;
import cn.gdeiassistant.Tools.Utils.StringEncryptUtils;
import cn.gdeiassistant.Tools.Utils.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WechatUserDataService {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "wechatUserMapper")
    private WechatUserMapper wechatUserMapper;

    /**
     * 查询微信账号绑定校园网络账号用户名
     *
     * @param wechatID
     * @return
     */
    public String QueryWechatAttachUsername(String wechatID) throws Exception {
        String username = wechatUserMapper.selectUsername(wechatID);
        if (StringUtils.isNotBlank(username)) {
            return StringEncryptUtils.decryptString(username);
        }
        return null;
    }

    /**
     * 通过WechatID对应的Username查询用户信息数据
     *
     * @param username
     * @return
     */
    public User QueryWechatUserData(String username) throws Exception {
        User queryUser = userMapper.selectUser(StringEncryptUtils.encryptString(username));
        if (queryUser != null) {
            return queryUser.decryptUser();
        }
        return null;
    }

    /**
     * 同步微信账号与校园网络账号绑定的数据
     *
     * @param username
     * @param wechatUserID
     * @return
     */
    public void SyncWechatUserData(String username, String wechatUserID) throws Exception {
        if (wechatUserMapper.selectUsername(wechatUserID) != null) {
            //更新微信账号绑定的校园网络账号
            wechatUserMapper.updateWechatUser(wechatUserID, StringEncryptUtils.encryptString(username));
        } else {
            wechatUserMapper.insertWechatUser(wechatUserID, StringEncryptUtils.encryptString(username));
        }
    }
}
