package com.ctrip.xpipe.redis.keeper.applier.sequence;

import com.ctrip.xpipe.redis.keeper.applier.command.ApplierRedisCommand;

/**
 * @author Slight
 * <p>
 * Jan 29, 2022 4:11 PM
 */
public interface ApplierSequenceController {

    void submit(ApplierRedisCommand<?> command);
}