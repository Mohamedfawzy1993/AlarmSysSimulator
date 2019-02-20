package controller.restcontroller;


import controller.impl.AlarmControllerImpl;
import controller.websocket.WebSocketServer;
import model.dto.Alarm;
import model.dto.Pagination;
import model.dto.ResultSet;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.List;

@Stateless
@Path("alarm")
public class AlarmRestController {

    @Inject
    private AlarmControllerImpl alarmController;
    @Inject
    private WebSocketServer webSocketServer;
    @GET
    public ResultSet<Alarm> findAlarmByID(@QueryParam("id") String alarmID ,
                                          @QueryParam("currentPage") int currentPage,
                                          @QueryParam("pageSize") int pageSize){
        Pagination pagination = null;
        if(pageSize != 0)
            pagination = new Pagination(pageSize , 0,currentPage);
        ResultSet<Alarm> alarmResultSet = alarmController.searchAlarmByID(alarmID ,pagination);
        alarmResultSet.getPagination().setCurrentPage(currentPage);
        alarmResultSet.getPagination().setPageSize(pageSize);
        return alarmResultSet;
    }

    @GET
    @Path("all")
    public ResultSet<Alarm> findAllAlarms(@QueryParam("id") String alarmID,
                                          @QueryParam("site") String site,
                                          @QueryParam("severity") String severity,
                                          @QueryParam("description") String description,
                                          @QueryParam("isActive") String isActive,
                                          @QueryParam("currentPage") int currentPage,
                                          @QueryParam("pageSize") int pageSize) {
        Alarm alarm = new Alarm();
        Pagination pagination = new Pagination(pageSize , 0 , currentPage);
        if(alarmID != null)
            alarm.setAlarmId(alarmID);
        if(site != null)
            alarm.setSiteId(site);
        if(severity != null)
            alarm.setSeverity(severity);
        if(description != null)
            alarm.setDescription(description);
        if(isActive != null)
            alarm.setIsActive(Integer.parseInt(isActive));

        ResultSet<Alarm> alarmResultSet = this.alarmController.findAllAlarms(alarm , pagination);
        return alarmResultSet;


    }


    @POST
    @Path("test")
    public void test(){
        ResultSet hashMap = this.alarmController.findAlarmsActiveVsCeased();
        System.out.println("In Test Service");
        this.webSocketServer.sendMessageToSessions(hashMap);
    }

}
