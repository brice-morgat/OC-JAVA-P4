package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    public void calculateFare(Ticket ticket, boolean isRecurring){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        //TODO: Correction made: retrieving a timestamp and converting it to an hour.
        double duration = (outHour - inHour) / 3600000;

        //Check if the duration is greater or less than 30 minutes
        if (duration <= 0.5) {
            duration = 0;
        }


        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                //Calcul du prix en fonction de la récurrence de l'utilisateur
                ticket.setPrice(isRecurring ? (duration * Fare.CAR_RATE_PER_HOUR) - ((duration * Fare.CAR_RATE_PER_HOUR) * Fare.DISCOUNT_RECURRING) : (duration * Fare.CAR_RATE_PER_HOUR));
                break;
            }
            case BIKE: {
                //Calcul du prix en fonction de la récurrence de l'utilisateur
                ticket.setPrice(isRecurring ? (duration * Fare.BIKE_RATE_PER_HOUR) - ((duration * Fare.BIKE_RATE_PER_HOUR) * Fare.DISCOUNT_RECURRING): (duration * Fare.BIKE_RATE_PER_HOUR));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}