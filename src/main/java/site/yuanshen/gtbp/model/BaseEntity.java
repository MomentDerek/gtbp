package site.yuanshen.gtbp.model;

import org.springframework.data.annotation.Version;

/**
 * TODO
 *
 * @author Moment
 */
public class BaseEntity {
    @Version
    Long version;
}
