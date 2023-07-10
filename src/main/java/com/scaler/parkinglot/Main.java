package com.scaler.parkinglot;

import com.scaler.parkinglot.controllers.TicketController;
import com.scaler.parkinglot.dto.GenerateTicketRequestDto;
import com.scaler.parkinglot.dto.GenerateTicketResponseDto;
import com.scaler.parkinglot.exceptions.NoParkingSpotAvailableException;
import com.scaler.parkinglot.models.Gate;
import com.scaler.parkinglot.models.Ticket;
import com.scaler.parkinglot.models.VehicleType;
import com.scaler.parkinglot.repositories.ParkingLotRepository;
import com.scaler.parkinglot.repositories.TicketRepository;
import com.scaler.parkinglot.services.GateService;
import com.scaler.parkinglot.services.TicketService;
import com.scaler.parkinglot.services.VehicleService;
import com.scaler.parkinglot.strategy.spotassignmentstrategy.RandomSpotAssignmentStrategy;
import com.scaler.parkinglot.strategy.spotassignmentstrategy.SpotAssignmentStrategy;

import javax.print.attribute.standard.JobKOctets;

public class Main {
    public static void main(String[] args) throws NoParkingSpotAvailableException {
        System.out.println("Hello world!");

        //Create the objects of all the services, repositories and controllers.
        //Dependencies.

        //Registry Design pattern -> To store all the objects at one place
        //So that we can use these objects whenever required.
        ObjectRegistry objectRegistry = new ObjectRegistry();
        VehicleService vehicleService = new VehicleService();
        GateService gateService = new GateService();
        TicketRepository ticketRepository = new TicketRepository();
        ParkingLotRepository parkingLotRepository = new ParkingLotRepository();
        SpotAssignmentStrategy spotAssignmentStrategy = new RandomSpotAssignmentStrategy(parkingLotRepository);
        TicketService ticketService = new TicketService(vehicleService,
                gateService, spotAssignmentStrategy, ticketRepository);
        TicketController ticketController = new TicketController(ticketService);

        objectRegistry.register("vehicleService", vehicleService);
        objectRegistry.register("gateService", gateService);
        objectRegistry.register("ticketRepository", ticketRepository);
        objectRegistry.register("parkingLotRepo", parkingLotRepository);
        objectRegistry.register("spotAssignmentStrategy", spotAssignmentStrategy);
        objectRegistry.register("ticketService", ticketService);
        objectRegistry.register("ticketController", ticketController);

        GenerateTicketRequestDto requestDto = new GenerateTicketRequestDto();
        requestDto.setVehicleType(VehicleType.SMALL);
        requestDto.setVehicleNumber("HR16X11234");
        requestDto.setGateId(123L);

        GenerateTicketResponseDto responseDto = ticketController.generateTicket(requestDto);

        Ticket ticket = responseDto.getTicket();
        //We have got the Ticket, do whatever you want !!
    }
}

//First start coding the Models and then code the requirements one by one.

// Issues with generateTicket method :

// 1. If we add more params in the input, then the current users will start failing.
// 2. Models are the internal details of my system, and we might not want to expose them to the external world.
// 3. Client will have to make lot of GET calls to create the vehicle and other objects.

// DTO -> Data Transfer Objects.
// request -> RequestDTO
// response -> responseDTO

//Dependencies -> Topological Sort
//SpringBoot
//Dependency graph, apply the topological sort on the garph and
// find the order in which the dependencies should be resolved.
// A->B->C_D