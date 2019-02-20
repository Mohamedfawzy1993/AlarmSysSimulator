package controller.impl;

import controller.interfaces.AlarmController;
import model.dao.AlarmDao;
import model.dto.Alarm;
import model.dto.Pagination;
import model.dto.ResultSet;
import model.dto.Status;
import util.TimeStampUtil;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

//@Local(AlarmController.class)
@Stateless
@LocalBean
public class AlarmControllerImpl implements AlarmController {

    @Inject
    private AlarmDao alarmDao;

    List<String> severities = new ArrayList<String>();


    @PostConstruct
    public void init(){
        severities.add("warning");
        severities.add("minor");
        severities.add("major");
        severities.add("critical");
    }


    public ResultSet<Alarm> searchAlarmByID(String alarmID , Pagination pagination) {
        List<Alarm> alarm = this.alarmDao.getAlarmsByID(alarmID , pagination);
        ResultSet<Alarm> alarmResultSet = new ResultSet<Alarm>();
        alarmResultSet.setData(alarm);
        alarmResultSet.getPagination().setTotal(alarm.size());
        return alarmResultSet;
    }

    public ResultSet<Alarm> findAllAlarms(Alarm criteriaObj , Pagination pagination) {
        List<Alarm> alarm = this.alarmDao.getAllAlarmsByCriteria(criteriaObj , pagination);
        ResultSet<Alarm> alarmResultSet = new ResultSet<Alarm>();
        alarmResultSet.setData(alarm);
        alarmResultSet.getPagination().setTotal(alarm.size());
        return alarmResultSet;

    }

    public void generateNewAlarm() {
        Alarm alarm = new Alarm();
        int siteNumber = new Random().nextInt(999);
        int criticalIndex = new Random().nextInt(4);

        // Alarm Object Values
        alarm.setAlarmId(Long.valueOf(System.currentTimeMillis()).toString());
        alarm.setSiteId("Cai"+siteNumber);
        alarm.setIsActive(1);
        alarm.setDescription("Description");
        alarm.setSeverity(severities.get(criticalIndex));
        alarm.setEventTime(TimeStampUtil.CURRENT_TIME());

        Status status = new Status();
        status.setStatus("active");
        status.setStatusChangeTimestamp(TimeStampUtil.CURRENT_TIME());
        status.setAlarmByAlarmId(alarm);

        HashSet<Status> statusHashSet = new HashSet<>();
        statusHashSet.add(status);
        alarm.setStatusesById(statusHashSet);

        this.alarmDao.insert(alarm);
    }

    public void ceaseRandomActiveAlarm() {

        List<Alarm> alarms = this.alarmDao.getAllActiveAlarms();
        if(alarms != null && alarms.size()>0) {

            int randomAlarmIndex = new Random().nextInt(alarms.size());
            Alarm randomAlarm = alarms.get(randomAlarmIndex);
            Status status = new Status();
            System.out.println(randomAlarm);
            // Cease Alarm
            randomAlarm.setIsActive(0);
            randomAlarm.setCeaseTime(TimeStampUtil.CURRENT_TIME());
            randomAlarm.addStatusesByID(status);             // Add Status to Alarm

            // Create New Status for Alarm
            status.setStatus("ceased");
            status.setStatusChangeTimestamp(TimeStampUtil.CURRENT_TIME());
            status.setAlarmByAlarmId(randomAlarm);


            this.alarmDao.update(randomAlarm);
        }
    }

    public ResultSet<HashMap<String , Long>> findAlarmsCountBySeverity (){
        return this.alarmDao.getAlarmsCountBySeverity();
    }

    public ResultSet<HashMap<String, Long>> findAlarmsActiveVsCeased (){
        return this.alarmDao.getAlarmsActiveVsCeased();
    }

    public ResultSet<HashMap<String, Long>> findTopAlarmSites(){
        return this.alarmDao.getTopAlarmSites();
    }



}
