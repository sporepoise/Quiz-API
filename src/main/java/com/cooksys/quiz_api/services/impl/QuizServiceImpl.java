package com.cooksys.quiz_api.services.impl;

import java.util.List;
import java.util.Random;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.*;
import com.cooksys.quiz_api.mappers.AnswerMapper;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.AnswerRepository;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.math.*;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

	private final QuizRepository quizRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	private final QuizMapper quizMapper;
	private final QuestionMapper questionMapper;
	private final AnswerMapper answerMapper;

	@Override
	public List<QuizResponseDto> getAllQuizzes() {
		return quizMapper.entitiesToDtos(quizRepository.findAll());
	}

	@Override
	public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {

		// Map our new quiz to to request dto
		Quiz newQuiz = quizMapper.requestDtoToEntity(quizRequestDto);
		// save this quiz to the database
		quizRepository.saveAndFlush(newQuiz);

		for (Question q : newQuiz.getQuestions()) {
			questionRepository.saveAndFlush(q); // save the question in the db
			q.setQuiz(newQuiz); // add the question to the new quiz
			for (Answer a : q.getAnswers()) {

				answerRepository.saveAndFlush(a); // save the answer to the db
				a.setQuestion(q); // add the answer to the question
			}
		}
		// return the entity as a response dto using the mapper
		return quizMapper.entityToResponseDto(newQuiz);
	}

	// Method to return a quiz based on a given id
	private Quiz getQuizById(Long id) {
		return quizRepository.getById(id);
	}

	public QuizResponseDto deleteQuiz(Long id) {

		// get the quiz entity to delete
		Quiz quizToDelete = getQuizById(id);

		// delete each question associated with the quiz
		for (Question q : quizToDelete.getQuestions()) {

			// delete each answer associated with the question
			for (Answer a : q.getAnswers()) {
				answerRepository.delete(a); //delete child first
			}
			questionRepository.delete(q);//delete parent
		}
		// delete the quiz from the database
		quizRepository.delete(quizToDelete);

		// map and return the quiz that was deleted as a response dto
		return quizMapper.entityToResponseDto(quizToDelete);

	}

//Update Quiz
	@Override
	public QuizResponseDto updateQuiz(Long id, String newName) {
		// Get the quiz to update
		Quiz quizToUpdate = getQuizById(id);
		quizToUpdate.setName(newName);
		quizRepository.saveAndFlush(quizToUpdate);

		return quizMapper.entityToResponseDto(quizToUpdate);
	}

//Random Question
	@Override
	public QuestionResponseDto getRandomQuestion(Long id) {

		// get the ids of all the questions for the given quiz id using a query
		List<Long> ids = questionRepository.findQuestionId(id);

		Random random = new Random(); // random number generator

		// will generate a random index and then use that index to get an id from ids
		Long randomId = ids.get(random.nextInt(ids.size()));
		System.out.println("randomId" + randomId);

		// Get the random question from the repository
		Question randomQuestion = questionRepository.getById(randomId);

		return questionMapper.entityToDto(randomQuestion);
	}

//Adds a question to the specified quiz
	@Override
	public QuestionResponseDto addQuestion(Long id, QuestionRequestDto questionRequestDto) {
		// get the quiz based on the id
		Quiz quiz = getQuizById(id);
		Question question = questionMapper.requestDtoToEntity(questionRequestDto);

		// add question to database
		questionRepository.saveAndFlush(question);

		// add question to quiz questions list
		quiz.getQuestions().add(question);

		return questionMapper.entityToDto(question);
	}

	@Override
	public QuestionResponseDto deleteQuestion(Long id1, Long id2) {
		// get the quiz based on the id
		Quiz quiz = getQuizById(id1);
		List<Question> questions = quiz.getQuestions();
		Question questionToDelete = null;
		
		for(Question q : questions) {
			if (q.getId().equals(id2)) {
				questionToDelete = q;
				for(Answer a: questionToDelete.getAnswers()) {
					//delete the answers for each question
					answerRepository.delete(a);
				}
				break;
			}
		}
		//delete from database
		questionRepository.delete(questionToDelete);
		
		return questionMapper.entityToDto(questionToDelete);
	}
	
	

}
