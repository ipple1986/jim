package usecase.of.zookeeper.barrier;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class DisttributedDivideTaskRunnable extends AbstractZooKeeperHelper implements Runnable, Watcher {


    public DisttributedDivideTaskRunnable(){
        super();
        try {
            zk.exists(barrierZnode,this);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(isBarrier){
            System.out.println("Divide Operator is watting ");
        }
        System.out.println("divide operator: 10/2="+(10/2));
        close();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType()== Event.EventType.NodeDeleted){
            isBarrier = Boolean.FALSE;
        }
    }
}
