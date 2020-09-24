package no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.dto;


import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.TaskParametereDto;

public class NewProsessTaskDto {

    protected String taskType;
    protected TaskParametereDto taskParametre;

    public NewProsessTaskDto(String taskType, String batchrunnername){
        this.taskType = taskType;
        taskParametre = new TaskParametereDto(batchrunnername);
    }
}
