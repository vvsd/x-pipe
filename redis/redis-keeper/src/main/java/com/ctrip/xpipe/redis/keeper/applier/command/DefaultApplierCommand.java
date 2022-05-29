package com.ctrip.xpipe.redis.keeper.applier.command;

import com.ctrip.xpipe.client.redis.AsyncRedisClient;
import com.ctrip.xpipe.command.AbstractCommand;
import com.ctrip.xpipe.redis.core.redis.operation.RedisKey;
import com.ctrip.xpipe.redis.core.redis.operation.RedisOp;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Slight
 * <p>
 * Feb 26, 2022 3:13 PM
 */
public class DefaultApplierCommand extends AbstractCommand<Boolean> implements ApplierRedisOpCommand<Boolean> {

    public static String ERR_GTID_COMMAND_EXECUTED = "ERR gtid command is executed";

    final AsyncRedisClient client;

    final Object resource;

    final RedisOp redisOp;

    public DefaultApplierCommand(AsyncRedisClient client, RedisOp redisOp) {
        this(client, null, redisOp);
    }

    public DefaultApplierCommand(AsyncRedisClient client, Object resource, RedisOp redisOp) {

        this.client = client;
        this.resource = resource;
        this.redisOp = redisOp;
    }

    @Override
    protected void doExecute() throws Throwable {

        Object rc = resource != null ? resource : client.select(key().get());
        Object[] rawArgs = redisOp.buildRawOpArgs().toArray();

        client
                .write(rc, rawArgs)
                .addListener(f->{
                    if (f.isSuccess()) {
                        future().setSuccess(true);
                    } else {
                        if (f.cause().getMessage().startsWith(ERR_GTID_COMMAND_EXECUTED)) {
                            future().setSuccess(true);
                        } else {
                            future().setFailure(f.cause());
                        }
                    }
                });
    }

    @Override
    protected void doReset() {

    }

    @Override
    public List<RedisOpCommand<Boolean>> sharding() {
        if (type().equals(RedisOpCommandType.MULTI_KEY)) {
            List<Object> keys = keys().stream().map(RedisKey::get).collect(Collectors.toList());
            return client.selectMulti(keys).entrySet().stream().map(e->
                    new DefaultApplierCommand(client,
                            /* resource */ e.getKey(),
                            /* subOp */ redisOpAsMulti().subOp(e.getValue().stream().map(keys::indexOf).collect(Collectors.toSet())))).collect(Collectors.toList());
        }
        return Lists.newArrayList(this);
    }

    @Override
    public RedisOp redisOp() {
        return redisOp;
    }
}