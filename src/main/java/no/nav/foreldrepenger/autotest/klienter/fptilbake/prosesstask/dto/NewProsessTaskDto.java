package no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.TaskParametereDto;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NewProsessTaskDto {

    protected String taskType;
    protected TaskParametereDto taskParametre;

    public NewProsessTaskDto(String taskType, String arg1, String arg2, String arg3, String arg4){
        this.taskType = taskType;
        taskParametre = new TaskParametereDto(arg1, arg2, arg3, arg4);
    }

    public NewProsessTaskDto(String taskType){
        this.taskType = taskType;
    }
}
