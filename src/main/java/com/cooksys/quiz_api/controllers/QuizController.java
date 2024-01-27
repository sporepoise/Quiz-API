package com.cooksys.quiz_api.controllers;

import java.util.List;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.services.QuizService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

  private final QuizService quizService;

  @GetMapping
  public List<QuizResponseDto> getAllQuizzes() {
    return quizService.getAllQuizzes();
  }
  //POST - create a quiz
  @PostMapping
	public QuizResponseDto createQuiz(@RequestBody QuizRequestDto quizRequestDto) {
		return quizService.createQuiz(quizRequestDto);
	}
  
  //DELETE - delete a given quiz by id
  @DeleteMapping("/{id}")
  public QuizResponseDto deleteQuiz(@PathVariable Long id) {
	  return quizService.deleteQuiz(id);
  }
  
  //PATCH - update the name of a specified quiz
  @PatchMapping("/{id}/rename/{newName}")
  public QuizResponseDto updateQuiz(@PathVariable Long id, @PathVariable String newName) {
	  return quizService.updateQuiz(id, newName);
  }
  
  //GET - get a random question from the specified quiz
  @GetMapping("/{id}/random")
  public QuestionResponseDto getRandomQuestion(@PathVariable Long id) {
	  return quizService.getRandomQuestion(id);
  }
  
  //PATCH - adds a question to the specified quiz
  @PatchMapping("/{id}/add")
  public QuestionResponseDto addQuestion(@PathVariable Long id, @RequestBody QuestionRequestDto questionRequestDto) {
	  return quizService.addQuestion(id,questionRequestDto);
  }
  
  //DELETE - deletes the specified question from the specified quiz
  
  @DeleteMapping("/{id1}/delete/{id2}")
  public QuestionResponseDto deleteQuestion(@PathVariable Long id1, @PathVariable Long id2) {
	  return quizService.deleteQuestion(id1,id2);
  }

}
