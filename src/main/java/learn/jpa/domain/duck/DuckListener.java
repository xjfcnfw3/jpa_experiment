package learn.jpa.domain.duck;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;

public class DuckListener {

    @PostLoad
    private void postLoad(Object obj) {
        System.out.println("DuckListener.postLoad obj = [" + obj + "]");
    }

    @PrePersist
    private void prePersist(Object obj) {
        System.out.println("DuckListener.prePersist obj = [" + obj + "]");
    }

    @PostPersist
    private void postPersist(Object obj) {
        System.out.println("DuckListener.postPersist obj = [" + obj + "]");
    }

    @PreRemove
    private void preRemove(Object obj) {
        System.out.println("DuckListener.preRemove obj = [" + obj + "]");
    }

    @PostRemove
    private void postRemove(Object obj) {
        System.out.println("DuckListener.postRemove obj = [" + obj + "]");
    }
}
