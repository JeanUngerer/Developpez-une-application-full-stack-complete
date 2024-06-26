package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.exceptions.BadRequestExceptionHandler;
import com.openclassrooms.mddapi.exceptions.NotFoundExceptionHandler;
import com.openclassrooms.mddapi.model.MddUser;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class TopicService is a service for handling various operations related to Topics.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class TopicService {

    TopicRepository topicRepository;

    /**
     * Method to return a list of all topics in the repository.
     * @return list of all topics
     */
    public List<Topic> findAllTopics() {
        try {
            log.info("findAllTopics");
            List<Topic> topicList = new ArrayList<>();
            topicRepository.findAll().forEach(tp -> topicList.add(tp));
            return topicList;
        } catch (Exception e) {
            log.error("We could not find all topics: " + e.getMessage());
            throw new NotFoundExceptionHandler("We could not find any topics");
        }
    }

    /**
     * Method to find and return a topic by its ID.
     * @param id Topic's ID.
     * @return the topic with given ID
     */
    public Topic findTopicById(Long id) {
        try {
            log.info("findTopicById - id: " + id);
            Topic topic = topicRepository.findById(id)
                    .orElseThrow(() -> new NotFoundExceptionHandler("Topic not found"));
            return topic;
        } catch (Exception e) {
            log.error("We could not find topic: " + id, e.getMessage());
            throw new NotFoundExceptionHandler("We could not find your topic");
        }
    }

    /**
     * Method to create a new topic.
     * @param topic Topic object.
     * @return created topic
     */
    public Topic createTopic(Topic topic) {
        try {
            log.info("createTopic");
            topic.setId(null);
            topic = topicRepository.save(topic);
            return topic;
        } catch (Exception e) {
            log.error("Failed to create topic: ", e.getMessage());
            throw new BadRequestExceptionHandler("Failed to create topic");
        }
    }

    /**
     * Method to update an existing topic.
     * @param topic Topic object with new data.
     * @return updated topic
     */
    public Topic updateTopic(Topic topic) {
        try {
            log.info("updateTopic - id: " + topic.getId());
            Topic existingTopic = topicRepository.findById(topic.getId())
                    .orElseThrow(() -> new NotFoundExceptionHandler("Topic not found"));
            existingTopic.setName(topic.getName());
            topicRepository.save(existingTopic);
            return existingTopic;
        } catch (Exception e) {
            log.error("Failed to update topic: ", e.getMessage());
            throw new BadRequestExceptionHandler("Failed to update topic");
        }
    }

    /**
     * Method to delete a topic.
     * @param id Topic's ID.
     * @return deletion message
     */
    public String deleteTopic(Long id) {
        try {
            log.info("deleteTopic - id: " + id);
            Topic topic = topicRepository.findById(id)
                    .orElseThrow(() -> new NotFoundExceptionHandler("Topic not found"));
            topicRepository.delete(topic);
            return "Topic deleted";
        } catch (Exception e) {
            log.error("Failed to delete topic: ", e.getMessage());
            throw new BadRequestExceptionHandler("Failed to delete topic");
        }
    }

    /**
     * Method for user to subscribe a topic.
     * @param topicId ID of the topic to subscribe.
     * @param mddUser User who wants to subscribe.
     * @return success message
     */
    public String subscribe(Long topicId, MddUser mddUser){
        try {
            log.info("User(id) : " + mddUser.getId() + "subscribe to topic(id) : " + topicId);
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new NotFoundExceptionHandler("Topic not found"));
            if (topic.getUsers() == null) {
                topic.setUsers(new HashSet<>());
            }
            topic.getUsers().add(mddUser);
            topicRepository.save(topic);
            return "Subscribed successfully";
        } catch(Exception e) {
            log.error("Failed to subscribe user(id) : " + mddUser.getId() + " to topic(id) : " + topicId, e.getMessage());
            throw new BadRequestExceptionHandler("Failed to subscribe to topic");
        }
    }

    /**
     * Method for user to unsubscribe a topic.
     * @param topicId ID of the topic to unsubscribe.
     * @param mddUser User who wants to unsubscribe.
     * @return success message
     */
    public String unsubscribe(Long topicId, MddUser mddUser){
        try {
            log.info("User(id) : " + mddUser.getId() + "unsubscribe from topic(id) : " + topicId);
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new NotFoundExceptionHandler("Topic not found"));
            if (topic.getUsers() != null) {
                topic.getUsers().remove(mddUser);
            }
            topicRepository.save(topic);
            return "Unsubscribed successfully";
        } catch(Exception e) {
            log.error("Failed to unsubscribe user(id) : " + mddUser.getId() + " from topic(id) : " + topicId, e.getMessage());
            throw new BadRequestExceptionHandler("Failed to unsubscribe from topic");
        }
    }

    /**
     * Method for user to get his subscriptions.
     * @param mddUser User who wants to get his subscriptions.
     * @return set of topics user is subscribed to
     */
    public Set<Topic> mySubscriptions(MddUser mddUser){
        try {
            log.info("Get my subscriptions for user(id) : " + mddUser.getId());
            return new HashSet<>(topicRepository.findTopicsByUsersId(mddUser.getId()));
        } catch (Exception e) {
            log.error("Failed to fetch subscriptions for user(id) : " + mddUser.getId(), e.getMessage());
            throw new NotFoundExceptionHandler("Failed to get your subscriptions");
        }
    }
}