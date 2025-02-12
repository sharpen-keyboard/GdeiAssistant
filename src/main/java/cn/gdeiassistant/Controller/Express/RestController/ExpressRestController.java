package cn.gdeiassistant.Controller.Express.RestController;

import com.taobao.wsgsvr.WsgException;
import cn.gdeiassistant.Exception.DatabaseException.DataNotExistException;
import cn.gdeiassistant.Exception.ExpressException.CorrectRecordException;
import cn.gdeiassistant.Exception.ExpressException.NoRealNameException;
import cn.gdeiassistant.Pojo.Entity.Express;
import cn.gdeiassistant.Pojo.Entity.ExpressComment;
import cn.gdeiassistant.Pojo.Result.DataJsonResult;
import cn.gdeiassistant.Pojo.Result.JsonResult;
import cn.gdeiassistant.Service.Express.ExpressService;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ExpressRestController {

    @Autowired
    private ExpressService expressService;

    /**
     * 分页查询表白信息
     *
     * @param start
     * @param size
     * @return
     * @throws WsgException
     */
    @RequestMapping(value = "/api/express/start/{start}/size/{size}", method = RequestMethod.GET)
    public DataJsonResult<List<Express>> QueryExpressPage(HttpServletRequest request, @PathVariable("start") int start
            , @PathVariable("size") int size) throws WsgException {
        List<Express> expressList = expressService.QueryExpressPage(start, size, request.getSession().getId());
        return new DataJsonResult<>(true, expressList);
    }

    /**
     * 分页关键词查询表白信息
     *
     * @param request
     * @param keyword
     * @param start
     * @param size
     * @return
     * @throws WsgException
     */
    @RequestMapping(value = "/api/express/keyword/{keyword}/start/{start}/size/{size}", method = RequestMethod.GET)
    public DataJsonResult<List<Express>> QueryExpressPageByKeyWord(HttpServletRequest request, @PathVariable("keyword") String keyword
            , @PathVariable("start") int start, @PathVariable("size") int size) throws WsgException {
        List<Express> expressList = expressService.QueryExpressPageByKeyword(request.getSession().getId(), start, size, keyword);
        return new DataJsonResult<>(true, expressList);
    }

    /**
     * 发布表白信息
     *
     * @param request
     * @param express
     * @return
     * @throws WsgException
     */
    @RequestMapping(value = "/api/express", method = RequestMethod.POST)
    public JsonResult AddExpress(HttpServletRequest request, @Validated Express express) throws WsgException {
        expressService.AddExpress(express, request.getSession().getId());
        return new JsonResult(true);
    }

    /**
     * 查询表白评论
     *
     * @return
     * @throws WsgException
     */
    @RequestMapping(value = "/api/express/id/{id}/comment", method = RequestMethod.GET)
    public DataJsonResult<List<ExpressComment>> QueryExpressComment(HttpServletRequest request, @PathVariable("id") Integer id) throws WsgException {
        List<ExpressComment> expressCommentList = expressService.QueryExpressComment(id);
        return new DataJsonResult<>(true, expressCommentList);
    }

    /**
     * 添加表白评论
     *
     * @param request
     * @param id
     * @param comment
     * @return
     * @throws DataNotExistException
     * @throws WsgException
     */
    @RequestMapping(value = "/api/express/id/{id}/comment", method = RequestMethod.POST)
    public JsonResult AddExpressComment(HttpServletRequest request, @PathVariable("id") Integer id
            , @Validated @NotBlank @Length(min = 1, max = 50) String comment) throws DataNotExistException, WsgException {
        expressService.AddExpressComment(id, request.getSession().getId(), comment);
        return new JsonResult(true);
    }

    /**
     * 提交猜一下表白信息
     *
     * @param request
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/express/id/{id}/guess", method = RequestMethod.POST)
    public DataJsonResult<Boolean> GuessExpress(HttpServletRequest request, @PathVariable("id") int id
            , @Validated @NotBlank @Length(min = 1, max = 10) String name) throws DataNotExistException, WsgException, NoRealNameException, CorrectRecordException {
        boolean result = expressService.GuessExpress(id, request.getSession().getId(), name);
        return new DataJsonResult<>(true, result);
    }

    /**
     * 点赞表白信息
     *
     * @param request
     * @param id
     * @return
     * @throws WsgException
     */
    @RequestMapping(value = "/api/express/id/{id}/like", method = RequestMethod.POST)
    public JsonResult LikeExpress(HttpServletRequest request, @PathVariable("id") int id) throws WsgException, DataNotExistException {
        expressService.LikeExpress(id, request.getSession().getId());
        return new JsonResult(true);
    }
}
