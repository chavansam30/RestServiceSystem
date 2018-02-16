package com.dervan.module.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.dervan.module.model.dao.EventData;
import com.dervan.module.model.dao.Game;
import com.dervan.module.model.dao.Participant;
import com.dervan.module.model.dao.Record;
import com.dervan.module.model.dao.RecordInner;
import com.dervan.module.model.dao.TeamGame;
import com.dervan.module.model.dao.TeamRecord;
import com.dervan.module.model.dao.TeamRecordInner;
import com.dervan.module.payment.IndividualPayment;
import com.dervan.module.payment.TeamPayment;
import com.dervan.module.update.UpdateIndividualDetails;
import com.dervan.module.update.UpdateTeamDetails;
import com.dervan.module.util.dao.HibernateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

@Path("/updateDetails")
public class UpdateDetailsController {
	
	
	@POST
	@Path("/participant")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public  Map<String, Map<String, Object>> getStoredDetails(Map<String, Integer> inputData) throws JsonProcessingException{
		
		
		Session session  = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
    	Map<String, Object> data = new HashMap<>();
    	Map<String, Map<String, Object>> parentMap = new HashMap<String, Map<String,Object>>();
		Participant participantData = null;
		List<EventData> partiGameData = null;
		
		if(inputData != null){
			
			participantData = IndividualPayment.getParticipant(inputData.get("partid"), session, tx);
			partiGameData = IndividualPayment.getPartiGameData(inputData.get("partid"), session, tx);
		}
		
		data.put("partidetails", participantData);
		//data.put("payData", payData);
		data.put("games", partiGameData);
		parentMap.put("record", data);
		
		tx.commit();
		session.close();
		
		return parentMap;
	}
	
	
	@POST
	@Path("/partUpdate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Record updateStoredDetails(Record inputData){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		
		Record record = null;
		RecordInner recordInner = null;
		Participant participant = null;
		String amount = null;
		String type = null;
		List<Game> partiGameData = null;
	
		
		
		
		if(inputData != null){
			record = (Record) inputData;
			participant = record.getRecord().getPartidetails();
			partiGameData = record.getRecord().getGames();
			amount = record.getRecord().getAmt();
			type = record.getRecord().getType();
			
		}
		
		recordInner = UpdateIndividualDetails.getUpdatedDetailsInd(participant, partiGameData, session, tx);
		recordInner.setAmt(amount);
		recordInner.setType(type);
		record.setRecord(recordInner);
		return record;
	}
	
	
/**
 * Author : Ajinkya
 * Description : Below part of code is for team aspirants;
 */
	
	@POST
	@Path("/team")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public  Map<String, Map<String, Object>> getStoredTeamDetails(Map<String, Integer> inputData) throws JsonProcessingException{
		
		
		Session session  = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
    	
		Map<String, Object> data = new HashMap<>();
    	Map<String, Map<String, Object>> parentMap = new HashMap<String, Map<String,Object>>();
    	
		Participant participantData = null;
		List<EventData> partiGameData = null;
		List<Participant> teamPartiData = null;
		
		if(inputData != null){
			
			participantData = TeamPayment.getCaptainData(inputData.get("captainid"), session, tx);
			partiGameData = TeamPayment.getTeamGameData(inputData.get("captainid"), session, tx);
			teamPartiData = TeamPayment.getParticipantsData(inputData.get("captainid"), session, tx);
			
		}
		
		data.put("partidetails", participantData);
		//data.put("payData", payData);
		data.put("games", partiGameData);
		data.put("tm", teamPartiData);
		parentMap.put("record", data);
		
		tx.commit();
		session.close();
		
		return parentMap;
	}
	
	
	@POST
	@Path("/teamUpdate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TeamRecord updateTeamStoredDetails(TeamRecord inputData){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		
		TeamRecord record = null;
		TeamRecordInner recordInner = null;
		Participant participant = null;
		List<Participant> listOfParticipants = null;
		int amount = 0;
		String type = null;
		List<TeamGame> teamGameData = null;
	
		
		
		
		if(inputData != null){
			record = (TeamRecord) inputData;
			participant = record.getRecord().getPartidetails();
			teamGameData = record.getRecord().getGames();
			amount = record.getRecord().getAmt();
			type = record.getRecord().getType();
			listOfParticipants = record.getRecord().getTm();
			
		}
		
		recordInner = UpdateTeamDetails.getUpdatedDetailsTeam(listOfParticipants, participant, teamGameData, session, tx);
		recordInner.setAmt(amount);
		recordInner.setType(type);
		record.setRecord(recordInner);
		session.close();
		return record;
	}

}
