package readinglist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class MySchedulingConfigurer implements SchedulingConfigurer,JpaRepositoriesAware{

    private JpaRepositories repositories;
    private  ScheduledTaskRegistrar scheduledTaskRegistrar;
    //id  hostName taskName taskType  configValue
    class TaskConfig{
         private int id;
         private String hostName;
         private String taskType;
         private String taskName;
         private String configValue;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public String getTaskType() {
            return taskType;
        }

        public void setTaskType(String taskType) {
            this.taskType = taskType;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }
    }
    private Map<String, Task> taskName2TaskMappings = new ConcurrentHashMap<>();
    private Map<String, String> taskName2MarksMappings = new ConcurrentHashMap<>();

    private boolean fetchNewTask(List<TaskConfig> taskConfigs){

        if(taskConfigs==null || taskConfigs.size()<=0)return Boolean.FALSE;
        taskName2TaskMappings.clear();
        taskConfigs.forEach(taskConfig -> {
            if(!taskName2MarksMappings.containsKey(taskConfig.getTaskName())
        || !taskName2MarksMappings.get(taskConfig.getTaskName()).equals(taskConfig.getTaskType().concat(taskConfig.getConfigValue()))){
                taskName2MarksMappings.put(taskConfig.getTaskName(),taskConfig.getTaskType().concat(taskConfig.getConfigValue()));
                Task task = null;
                if(taskConfig.getTaskType().equals("cron")){
                    task = new CronTask(()->{},taskConfig.getConfigValue());
                }
                taskName2TaskMappings.put(taskConfig.getTaskName(),task);
            }
        });
        return taskName2TaskMappings.size()>0?Boolean.TRUE:Boolean.FALSE;
    }
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        scheduledTaskRegistrar = taskRegistrar;
        taskRegistrar.addCronTask(new MyCronTask("main",()->{

            boolean isChanged = fetchNewTask(null);
            //System.out.println(repositories.size() );
            System.out.println(scheduledTaskRegistrar.getCronTaskList().size());
            System.out.println(scheduledTaskRegistrar.getScheduledTasks().size());
            //System.out.println(scheduledTaskRegistrar.getFixedDelayTaskList().size());
            //System.out.println(scheduledTaskRegistrar.getFixedRateTaskList().size());

            System.out.println("====start======");
            scheduledTaskRegistrar.getScheduledTasks().forEach(task->{
                if(task.getTask() instanceof MyTask){
                    String taskName = (((MyTask) task.getTask()).taskName());
                    System.out.println(taskName);
                    if(!"main".equals(taskName)){
                        boolean re = scheduledTaskRegistrar.getCronTaskList().remove(task.getTask());
                        task.cancel();
                        System.out.println(re);
                    }
                }
            });
            System.out.println("====end======");
            MyCronTask  myCronTask =  new MyCronTask("bb",()->{
                System.out.println("---");
            },"0/10 * * * * ?");
            scheduledTaskRegistrar.addCronTask(myCronTask);
            scheduledTaskRegistrar.getScheduledTasks().add(scheduledTaskRegistrar.scheduleCronTask(myCronTask));
            /*if(isChanged){
                System.out.println(scheduledTaskRegistrar.getScheduledTasks().size());
                //scheduledTaskRegistrar.destroy();
                scheduledTaskRegistrar.getScheduledTasks().forEach(task->task.cancel());

                scheduledTaskRegistrar.addCronTask(new CronTask(()->{
                    System.out.println("new --");
                },new CronTrigger("0/5 * * * * ?")));

                scheduledTaskRegistrar.afterPropertiesSet();
            }*/

            },"0/5 * * * * ?"));
    }

    public void setRepositories(JpaRepositories repositories) {
        this.repositories = repositories;
    }
    interface MyTask{
       String taskName();
    }
    class MyCronTask extends CronTask implements MyTask{
        private String taskName;
        public MyCronTask(String taskName,Runnable runnable, String expression) {
            this(runnable, expression);
            this.taskName = taskName;
        }
        public MyCronTask(Runnable runnable, String expression) {
            super(runnable, expression);
        }

        @Override
        public String taskName() {
            return taskName;
        }
    }
}
