package com.gdeiassistant.gdeiassistant.Service.IPAddress;

import com.gdeiassistant.gdeiassistant.Pojo.Entity.Location;
import com.gdeiassistant.gdeiassistant.Service.CloudAPI.AliYunService;
import com.gdeiassistant.gdeiassistant.Service.CloudAPI.JiSuAPIService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class IPService {

    @Autowired
    private AliYunService aliYunService;

    @Autowired
    private JiSuAPIService jiSuAPIService;

    /**
     * 根据IP地址查询IP地址归属地
     *
     * @param ip
     * @return
     */
    public Location GetLocationInfoByIPAddress(String ip) {
        return jiSuAPIService.GetLocationInfoByIPAddress(ip);
    }

    /**
     * 通过Request请求获取IP地址
     *
     * @param request
     * @return
     */
    public String GetRequestRealIPAddress(HttpServletRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            return XFor;
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }

}
