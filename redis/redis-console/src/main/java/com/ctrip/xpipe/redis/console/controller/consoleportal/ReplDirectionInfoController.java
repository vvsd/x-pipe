package com.ctrip.xpipe.redis.console.controller.consoleportal;

import com.ctrip.xpipe.redis.console.controller.AbstractConsoleController;
import com.ctrip.xpipe.redis.console.model.ReplDirectionInfoModel;
import com.ctrip.xpipe.redis.console.service.ReplDirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AbstractConsoleController.CONSOLE_PREFIX)
public class ReplDirectionInfoController extends AbstractConsoleController {

    @Autowired
    ReplDirectionService replDirectionService;

    @RequestMapping("/repl-direction/cluster/" + CLUSTER_NAME_PATH_VARIABLE + "/src-dc/{srcDcName}/to-dc/{toDcName}")
    public ReplDirectionInfoModel getReplDirectionInfoModelByClusterAndSrcToDc(@PathVariable String clusterName,
                                                                               @PathVariable String srcDcName, @PathVariable String toDcName){
        return replDirectionService.findReplDirectionInfoModelByClusterAndSrcToDc(clusterName, srcDcName, toDcName);
    }

    @RequestMapping("/repl-direction/cluster/" + CLUSTER_NAME_PATH_VARIABLE)
    public List<ReplDirectionInfoModel> getAllReplDirectionInfoModelsByCluster(@PathVariable String clusterName) {
        return replDirectionService.findAllReplDirectionInfoModelsByCluster(clusterName);
    }

    @RequestMapping("/repl-direction/infos/all")
    public List<ReplDirectionInfoModel> getAllReplDirectionInfoModels() {
        return replDirectionService.findAllReplDirectionInfoModels();
    }
}
