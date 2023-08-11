package cn.addenda.bc.rbac.controller;

import cn.addenda.bc.bc.jc.util.AssertUtils;
import cn.addenda.bc.bc.jc.util.BeanUtil;
import cn.addenda.bc.bc.sc.result.ControllerResult;
import cn.addenda.bc.rbac.pojo.entity.Module;
import cn.addenda.bc.rbac.pojo.vo.VModule;
import cn.addenda.bc.rbac.service.ModuleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/module")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @GetMapping("/rootSqc")
    public ControllerResult<Long> rootSqc() {
        return ControllerResult.success(moduleService.rootSqc());
    }

    @PostMapping("/insert")
    public ControllerResult<Long> insert(@RequestBody VModule module) {
        AssertUtils.notNull(module);
        AssertUtils.notNull(module.getModuleCode(), "moduleCode");
        AssertUtils.notNull(module.getModuleName(), "moduleName");
        AssertUtils.notNull(module.getParentSqc(), "parentSqc");
        AssertUtils.notNull(module.getAction(), "action");
        AssertUtils.notNull(module.getShowType(), "showType");
        AssertUtils.notNull(module.getResponseToType(), "responseToType");

        return ControllerResult.success(moduleService.insert(BeanUtil.copyProperties(module, new Module())));
    }

    @GetMapping("/pageQuery")
    public ControllerResult<PageInfo<Module>> pageQuery(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VModule module) {
        AssertUtils.notNull(pageNum, "pageNum");
        AssertUtils.notNull(pageSize, "pageSize");
        AssertUtils.notNull(module);

        return ControllerResult.success(moduleService.pageQuery(pageNum, pageSize, BeanUtil.copyProperties(module, new Module())));
    }

    @GetMapping("/queryBySqc")
    public ControllerResult<Module> queryBySqc(@RequestParam("sqc") Long sqc) {
        AssertUtils.notNull(sqc, "sqc");

        return ControllerResult.success(moduleService.queryBySqc(sqc));
    }

    @PutMapping("/setStatus")
    public ControllerResult<Boolean> setStatus(@RequestParam("sqc") Long sqc, @RequestBody String status) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(status);

        return ControllerResult.success(moduleService.setStatus(sqc, status));
    }

    @PutMapping("/update")
    public ControllerResult<Boolean> update(@RequestParam("sqc") Long sqc, @RequestBody VModule module) {
        AssertUtils.notNull(sqc, "sqc");
        AssertUtils.notNull(module);
        AssertUtils.notNull(module.getModuleName(), "moduleName");
        AssertUtils.notNull(module.getAction(), "action");
        AssertUtils.notNull(module.getAccessType(), "accessType");
        AssertUtils.notNull(module.getShowType(), "showType");
        AssertUtils.notNull(module.getResponseToType(), "responseToType");

        AssertUtils.notModified(module.getModuleCode(), "moduleCode");
        AssertUtils.notModified(module.getParentSqc(), "parentSqc");

        return ControllerResult.success(moduleService.update(BeanUtil.copyProperties(module, new Module(sqc))));
    }

    @DeleteMapping("/deleteBySqc")
    public ControllerResult<Boolean> deleteBySqc(@RequestBody Long sqc) {
        AssertUtils.notNull(sqc);

        return ControllerResult.success(moduleService.deleteBySqc(sqc));
    }

}
