package cn.gdeiassistant.Service.Delivery;

import cn.gdeiassistant.Exception.DatabaseException.DataNotExistException;
import cn.gdeiassistant.Exception.DeliveryException.DeliveryOrderStateUpdatedException;
import cn.gdeiassistant.Exception.DeliveryException.DeliveryOrderTakenException;
import cn.gdeiassistant.Exception.DeliveryException.NoAccessUpdatingException;
import cn.gdeiassistant.Exception.DeliveryException.SelfTradingOrderException;
import cn.gdeiassistant.Pojo.Entity.DeliveryOrder;
import cn.gdeiassistant.Pojo.Entity.DeliveryTrade;
import cn.gdeiassistant.Pojo.Entity.User;
import cn.gdeiassistant.Repository.SQL.Mysql.Mapper.GdeiAssistant.Delivery.DeliveryMapper;
import cn.gdeiassistant.Service.UserLogin.UserCertificateService;
import cn.gdeiassistant.Tools.Utils.StringEncryptUtils;
import cn.gdeiassistant.Tools.Utils.StringUtils;
import com.taobao.wsgsvr.WsgException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DeliveryService {

    @Resource(name = "deliveryMapper")
    private DeliveryMapper deliveryMapper;

    @Autowired
    private UserCertificateService userCertificateService;

    /**
     * 分页查询订单信息
     *
     * @param start
     * @param size
     * @return
     */
    public List<DeliveryOrder> QueryDeliveryOrderPage(Integer start, Integer size) throws WsgException {
        List<DeliveryOrder> deliveryOrderList = deliveryMapper.selectDeliveryOrderPage(start, size);
        if (deliveryOrderList != null && !deliveryOrderList.isEmpty()) {
            for (DeliveryOrder deliveryOrder : deliveryOrderList) {
                deliveryOrder.setUsername(StringEncryptUtils.decryptString(deliveryOrder.getUsername()));
            }
            return deliveryOrderList;
        }
        return new ArrayList<>();
    }

    /**
     * 查询当前用户的所有下单信息
     *
     * @param sessionId
     * @return
     * @throws WsgException
     */
    public List<DeliveryOrder> QueryPersonalDeliveryOrder(String sessionId) throws WsgException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        List<DeliveryOrder> list = new ArrayList<>();
        List<DeliveryOrder> deliveryOrderList = deliveryMapper
                .selectDeliveryOrderByUsername(StringEncryptUtils.encryptString(user.getUsername()));
        if (deliveryOrderList != null && !deliveryOrderList.isEmpty()) {
            for (DeliveryOrder deliveryOrder : deliveryOrderList) {
                deliveryOrder.setUsername(StringEncryptUtils.decryptString(deliveryOrder.getUsername()));
            }
            list.addAll(deliveryOrderList);
        }
        return list;
    }

    /**
     * 查询当前用户的所有接受的下单信息
     *
     * @param sessionId
     * @return
     * @throws WsgException
     */
    public List<DeliveryOrder> QueryPersonalAcceptedDeliveryOrder(String sessionId) throws WsgException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        List<DeliveryOrder> list = new ArrayList<>();
        List<DeliveryOrder> acceptedDeliveryOrderList = deliveryMapper.selectAcceptedDeliveryOrderByUsername(StringEncryptUtils
                .encryptString(user.getUsername()));
        if (acceptedDeliveryOrderList != null && !acceptedDeliveryOrderList.isEmpty()) {
            for (DeliveryOrder deliveryOrder : acceptedDeliveryOrderList) {
                deliveryOrder.setUsername(StringEncryptUtils.decryptString(deliveryOrder.getUsername()));
            }
            list.addAll(acceptedDeliveryOrderList);
        }
        return list;
    }

    /**
     * 查询当前用户的所有接单信息
     *
     * @param username
     * @return
     */
    public List<DeliveryTrade> QueryPersonalDeliveryTrade(String username) throws WsgException {
        List<DeliveryTrade> deliveryTradeList = deliveryMapper.selectDeliveryTradeByUsername(StringEncryptUtils.encryptString(username));
        if (deliveryTradeList != null && !deliveryTradeList.isEmpty()) {
            for (DeliveryTrade deliveryTrade : deliveryTradeList) {
                deliveryTrade.setUsername(StringEncryptUtils.decryptString(deliveryTrade.getUsername()));
            }
            return deliveryTradeList;
        }
        return new ArrayList<>();
    }

    /**
     * 查询订单详细信息
     *
     * @param orderId
     */
    public DeliveryOrder QueryDeliveryOrderByOrderId(Integer orderId) throws DataNotExistException, WsgException {
        DeliveryOrder deliveryOrder = deliveryMapper.selectDeliveryOrderByOrderId(orderId);
        if (deliveryOrder != null) {
            deliveryOrder.setUsername(StringEncryptUtils.decryptString(deliveryOrder.getUsername()));
            return deliveryOrder;
        }
        throw new DataNotExistException("该快递代收订单不存在");
    }

    /**
     * 查询交易详细信息
     *
     * @param orderId
     * @return
     * @throws WsgException
     * @throws DataNotExistException
     */
    public DeliveryTrade QueryDeliveryTradeByOrderId(Integer orderId) throws WsgException, DataNotExistException {
        DeliveryTrade deliveryTrade = deliveryMapper.selectDeliveryTradeByOrderId(orderId);
        if (deliveryTrade != null) {
            deliveryTrade.setUsername(StringEncryptUtils.decryptString(deliveryTrade.getUsername()));
            return deliveryTrade;
        }
        throw new DataNotExistException("该快递代收订单不存在");
    }

    /**
     * 查询交易详细信息
     *
     * @param tradeId
     * @return
     * @throws DataNotExistException
     * @throws WsgException
     */
    public DeliveryTrade QueryDeliveryTradeByTradeId(Integer tradeId) throws DataNotExistException, WsgException {
        DeliveryTrade deliveryTrade = deliveryMapper.selectDeliveryTradeByTradeId(tradeId);
        if (deliveryTrade != null) {
            deliveryTrade.setUsername(StringEncryptUtils.decryptString(deliveryTrade.getUsername()));
            return deliveryTrade;
        }
        throw new DataNotExistException("该快递代收交易不存在");
    }

    /**
     * 查询当前用户组对于代收交易详细信息的权限类型
     * <p>
     * 返回的数值：DetailType表示信息详细程度
     * 0表示显示所有信息且显示确认交付按钮（下单者）
     * 1表示没有权限查看（第三方）
     * 2表示显示所有信息但没有操作按钮（接单者）
     *
     * @param sessionId
     * @param tradeId
     * @return
     */
    public int QueryDeliveryTradeDetailType(String sessionId, int tradeId) throws DataNotExistException, WsgException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        DeliveryTrade deliveryTrade = QueryDeliveryTradeByTradeId(tradeId);
        if (deliveryTrade.getUsername().equals(StringEncryptUtils.encryptString(user.getUsername()))) {
            //自己接受的交易单
            return 2;
        } else {
            //他人接受的交易单，检查当前用户是否为下单人
            if (CheckOrderPublisher(deliveryTrade.getOrderId(), sessionId)) {
                //当前用户为下单人，可以查看详细信息和交付按钮
                return 0;
            } else {
                //没有权限查看
                return 1;
            }
        }
    }

    /**
     * 查询当前用户组对于快递订单详细信息的权限类型
     * <p>
     * DetailType表示信息详细程度
     * 0表示显示所有信息和删除按钮（发布者）
     * 1表示显示大致信息和接单按钮（被接单前的第三方）
     * 2表示没有权限查看（被接单后的第三方）
     * 3表示显示所有信息但是没有操作按钮（接单者）
     *
     * @param sessionId
     * @param orderId
     * @throws DataNotExistException
     * @throws WsgException
     */
    public int QueryDeliveryOrderDetailType(String sessionId, int orderId) throws DataNotExistException, WsgException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        DeliveryOrder deliveryOrder = QueryDeliveryOrderByOrderId(orderId);
        if (deliveryOrder.getUsername().equals(StringEncryptUtils.encryptString(user.getUsername()))) {
            //自己发布的订单信息
            return 0;
        } else {
            //他人发布的订单信息
            if (deliveryOrder.getState().equals(0)) {
                //等待接单，可以查看
                return 1;
            } else {
                //已经接单，检查当前用户是否为接单人
                if (CheckOrderAccepter(deliveryOrder.getOrderId(), user.getUsername())) {
                    //当前用户为接单人，可以查看详细信息
                    return 3;
                } else {
                    //没有权限查看
                    return 2;
                }
            }
        }
    }

    /**
     * 用户抢单
     *
     * @param orderId
     * @param sessionId
     */
    public void AcceptOrder(Integer orderId, String sessionId) throws Exception {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        if (CheckAcceptUsername(orderId, user.getUsername())) {
            throw new SelfTradingOrderException("不能接受自己下的快递代收单");
        }
        UpdateOrderAndInsertTradeRecord(orderId, user.getUsername());
    }

    /**
     * 检查接单人和下单人的身份，防止自行接单
     */
    public boolean CheckAcceptUsername(Integer orderId, String username) throws WsgException {
        String orderUsername = deliveryMapper.selectDeliveryOrderUsername(orderId);
        return Objects.equals(StringEncryptUtils.encryptString(username), orderUsername);
    }

    /**
     * 接单数据层操作，更新订单状态，插入交易记录
     *
     * @param orderId
     * @param username
     */
    @Transactional("appTransactionManager")
    public void UpdateOrderAndInsertTradeRecord(Integer orderId, String username) throws Exception {
        //设置排他锁更新订单状态，并发情况下只允许一个线程修改该订单ID的订单状态
        int result = deliveryMapper.updateOrderState(orderId, 1);
        if (result > 0) {
            //若影响行数大于0，则表示抢单成功
            DeliveryTrade deliveryTrade = new DeliveryTrade();
            deliveryTrade.setUsername(StringEncryptUtils.encryptString(username));
            deliveryTrade.setOrderId(orderId);
            //插入交易记录
            deliveryMapper.insertTradeRecord(deliveryTrade);
        } else {
            //抢单失败
            throw new DeliveryOrderTakenException("抢单失败");
        }
    }

    /**
     * 检测该订单ID对应的接单人与提交的用户名是否匹配
     *
     * @param orderId
     * @param sessionId
     * @return
     */
    public boolean CheckOrderAccepter(Integer orderId, String sessionId) throws DataNotExistException, WsgException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        String accepterUsername = deliveryMapper.selectDeliveryTradeUsernameByOrderId(orderId);
        if (StringUtils.isBlank(accepterUsername)) {
            throw new DataNotExistException("该快递代收交易不存在");
        }
        return Objects.equals(StringEncryptUtils.decryptString(accepterUsername), user.getUsername());
    }

    /**
     * 检测该交易ID对应的下单人与提交的用户名是否匹配
     *
     * @param orderId
     * @param sessionId
     * @return
     * @throws WsgException
     */
    public boolean CheckOrderPublisher(Integer orderId, String sessionId) throws WsgException, DataNotExistException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        String publisherUsername = deliveryMapper.selectDeliveryOrderUsernameByOrderId(orderId);
        if (StringUtils.isBlank(publisherUsername)) {
            throw new DataNotExistException("该快递代收订单不存在");
        }
        return Objects.equals(StringEncryptUtils.decryptString(publisherUsername), user.getUsername());
    }

    /**
     * 删除订单信息
     *
     * @param orderId
     * @param sessionId
     * @throws DataNotExistException
     * @throws WsgException
     * @throws NoAccessUpdatingException
     */
    @Transactional("appTransactionManager")
    public void DeleteOrder(Integer orderId, String sessionId) throws DataNotExistException, WsgException, NoAccessUpdatingException, DeliveryOrderStateUpdatedException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        DeliveryOrder deliveryOrder = deliveryMapper.selectDeliveryOrderByOrderId(orderId);
        if (deliveryOrder != null) {
            if (deliveryOrder.getUsername().equals(StringEncryptUtils.encryptString(user.getUsername()))) {
                int result = deliveryMapper.deleteOrder(orderId);
                if (result == 0) {
                    throw new DeliveryOrderStateUpdatedException("订单状态已更新");
                }
                return;
            }
            throw new NoAccessUpdatingException("你没有权限修改该订单状态");
        }
        throw new DataNotExistException("该快递代收订单不存在");
    }

    /**
     * 完成快递代收交易
     *
     * @param tradeId
     * @param sessionId
     */
    public void FinishTrade(Integer tradeId, String sessionId) throws DataNotExistException, WsgException, NoAccessUpdatingException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        DeliveryTrade deliveryTrade = deliveryMapper.selectDeliveryTradeByTradeId(tradeId);
        if (deliveryTrade != null) {
            if (deliveryTrade.getOrderId() != null) {
                DeliveryOrder deliveryOrder = deliveryMapper.selectDeliveryOrderByOrderId(deliveryTrade.getOrderId());
                if (deliveryOrder != null) {
                    if (deliveryOrder.getUsername().equals(StringEncryptUtils.encryptString(user.getUsername()))) {
                        deliveryMapper.updateTradeState(tradeId, 1);
                        return;
                    }
                    throw new NoAccessUpdatingException("你没有权限修改该订单状态");
                }
            }
        }
        throw new DataNotExistException("该快递代收交易不存在");
    }

    /**
     * 添加快递代收订单，下单操作
     *
     * @param deliveryOrder
     */
    public void AddDeliveryOrder(String sessionId, DeliveryOrder deliveryOrder) throws WsgException {
        User user = userCertificateService.GetUserLoginCertificate(sessionId);
        DeliveryOrder order = new DeliveryOrder();
        order.setUsername(StringEncryptUtils.encryptString(user.getUsername()));
        order.setName(deliveryOrder.getName());
        order.setNumber(deliveryOrder.getNumber());
        order.setPhone(deliveryOrder.getPhone());
        order.setNumber(deliveryOrder.getNumber());
        order.setPrice(deliveryOrder.getPrice());
        order.setCompany(deliveryOrder.getCompany());
        order.setAddress(deliveryOrder.getAddress());
        order.setRemarks(deliveryOrder.getRemarks());
        order.setUsername(StringEncryptUtils.encryptString(deliveryOrder.getUsername()));
        order.setRemarks(deliveryOrder.getRemarks());
        deliveryMapper.insertDeliveryOrder(order);
    }
}
