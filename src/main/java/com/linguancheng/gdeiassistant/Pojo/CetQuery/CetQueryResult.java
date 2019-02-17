package com.gdeiassistant.gdeiassistant.Pojo.CetQuery;

import com.gdeiassistant.gdeiassistant.Enum.Base.ServiceResultEnum;
import com.gdeiassistant.gdeiassistant.Pojo.Entity.Cet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CetQueryResult {

    //四六级考试成绩查询结果
    private ServiceResultEnum cetQueryResultEnum;

    //四六级考试查询信息
    private Cet cet;

    public ServiceResultEnum getCetQueryResultEnum() {
        return cetQueryResultEnum;
    }

    public void setCetQueryResultEnum(ServiceResultEnum cetQueryResultEnum) {
        this.cetQueryResultEnum = cetQueryResultEnum;
    }

    public Cet getCet() {
        return cet;
    }

    public void setCet(Cet cet) {
        this.cet = cet;
    }
}
