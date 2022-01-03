package com.github.shoothzj.config.client.impl.postgre.spring;

import com.github.shoothzj.config.client.impl.postgre.spring.domain.ConfigNotifyPo;
import com.github.shoothzj.config.client.impl.postgre.spring.repository.ConfigNotifyRepository;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hezhangjian
 */
@Slf4j
public class ConfigNotifyScanner implements InitializingBean {

    @Autowired
    private ConfigNotifyRepository configNotifyRepository;

    @Autowired
    private CcImplPostgre ccImplPostgre;

    private long maxIdScanned;

    private final ScheduledExecutorService executorService =
            new ScheduledThreadPoolExecutor(1, new DefaultThreadFactory("notify-scanner"));

    public ConfigNotifyScanner() {
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        maxIdScanned = loadLargestMessageId();
        executorService.scheduleWithFixedDelay(() -> {
            try {
                scanMessages();
            } catch (Exception e) {
                log.error("schedule exception happened ", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Scan messages, continue scanning until there is no more messages
     */
    private void scanMessages() {
        boolean hasMoreMessages = true;
        while (hasMoreMessages && !Thread.currentThread().isInterrupted()) {
            hasMoreMessages = scanAndSendMessages();
        }
    }

    /**
     * scan messages and send
     *
     * @return whether there are more messages
     */
    private boolean scanAndSendMessages() {
        //current batch is 500
        List<ConfigNotifyPo> releaseMessages =
                configNotifyRepository.findFirst500ByIdGreaterThanOrderByIdAsc(maxIdScanned);
        if (CollectionUtils.isEmpty(releaseMessages)) {
            return false;
        }
        for (ConfigNotifyPo releaseMessage : releaseMessages) {
            ccImplPostgre.notify(releaseMessage.getConfigName(), releaseMessage.getConfigItemId());
        }
        int messageScanned = releaseMessages.size();
        maxIdScanned = releaseMessages.get(messageScanned - 1).getId();
        return messageScanned == 500;
    }

    /**
     * find largest message id as the current start point
     *
     * @return current largest message id
     */
    private long loadLargestMessageId() {
        ConfigNotifyPo releaseMessage = configNotifyRepository.findTopByOrderByIdDesc();
        return releaseMessage == null ? 0 : releaseMessage.getId();
    }

}
