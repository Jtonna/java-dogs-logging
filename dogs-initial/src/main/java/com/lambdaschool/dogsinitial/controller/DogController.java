package com.lambdaschool.dogsinitial.controller;

import com.lambdaschool.dogsinitial.exception.ResourceNotFoundException;
import com.lambdaschool.dogsinitial.model.Dog;
import com.lambdaschool.dogsinitial.DogsinitialApplication;
import com.lambdaschool.dogsinitial.model.MessageDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/dogs")
public class DogController
{
    private static final Logger logger = LoggerFactory.getLogger(DogController.class);
    // final means we would never change it. we need this line in every file we might want a log from

    @Autowired
    RabbitTemplate rt;

    // localhost:8080/dogs/dogs
    @GetMapping(value = "/dogs")
    public ResponseEntity<?> getAllDogs()
    {
        logger.info("/dogs/dogs accessed");

        MessageDetail message = new MessageDetail("/dogs/dogs accessed " + new Date(), 7, false);
        rt.convertAndSend(DogsinitialApplication.QUEUE_NAME_HIGH, message);

        return new ResponseEntity<>(DogsinitialApplication.ourDogList.dogList, HttpStatus.OK);
    }

    // localhost:8080/dogs/{id}
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDogDetail(@PathVariable long id)
    {
        logger.trace("/dogs/" + id + " was accessed");

                MessageDetail message = new MessageDetail("/dogs/" + id + " " + new Date(), 1, true);
        rt.convertAndSend(DogsinitialApplication.QUEUE_NAME_LOW, message);
        
        Dog rtnDog;
        if(DogsinitialApplication.ourDogList.findDog(d -> (d.getId() == id))== null){
            throw new ResourceNotFoundException("The dog with the id "+ id + " does not exist");
        } else{
            rtnDog = DogsinitialApplication.ourDogList.findDog(d -> (d.getId() == id));
        }
        return new ResponseEntity<>(rtnDog, HttpStatus.OK);
    }

    // localhost:8080/dogs/breeds/{breed}
    @GetMapping(value = "/breeds/{breed}")
    public ResponseEntity<?> getDogBreeds (@PathVariable String breed)
    {
        logger.trace("/dogs/breeeds/" + breed + " was accessed.");
        ArrayList<Dog> rtnDogs = DogsinitialApplication.ourDogList.
                findDogs(d -> d.getBreed().toUpperCase().equals(breed.toUpperCase()));
        return new ResponseEntity<>(rtnDogs, HttpStatus.OK);
    }

    // localhost:2019/dogs/dogtable
    @GetMapping(value = "/dogtable")
    public ModelAndView showDogTable(){
        logger.info("/dogs/dogtable was accessed.");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("dogs");
        mav.addObject("dogList", DogsinitialApplication.ourDogList.dogList);
        return mav;
    }
}
