package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;//first character in lexeme being scanned
  private int current = 0;//character currentlybeing considered
  private int line = 1;//source line current is on

  Scanner(String source) {
    this.source = source;
  }

  //scan tokens until EOF
  List<Token> scanTokens() {
    while(!isAtEnd()) {
      //at the beginning of the next lexeme
      start = current;
      scanToken();
    }
    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  //adds a new token to the list if expected
  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : EQUAL);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : EQUAL);
        break;
      case '/':
        if(match('/')) {
          //is comment, keep consuming characters to end of line
          while(peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
        break;

      case ' ':
      case '\r':
      case '\t':
        //ignore whitespace
        break;
      
      case '\n':
        line++;
        break;

      case '"': string(); break;

      default:
        if(isDigit(c)) {
          number();
        } else{
          Lox.error(line, "Unexpected character.");
        } 
        break;
    }
  }

  private void number() {
    while(isDigit(peek())) advance();

    //look for fractional
    if(peek() == '.' && isDigit(peekNext())) {
      advance();

      while(isDigit(peek())) advance();
    }
    addToken(NUMBER,
        Double.parseDouble(source.substring(start, current)));
  }

  private void string() {
    while(peek() != '"' && !isAtEnd()) {
      if(peek() == '\n') line++; //Lox supports multiline comments
      advance();
    }

    if(isAtEnd()) {
      Lox.error(line, "Undetermined string.");
      return;
    }

    //closing quote
    advance();

    //trim off surround quotes
    String value = source.substring(start + 1, current -1);
    addToken(STRING, value);
  }

  //look at second character for non-single-character lexemes
  //consumes the character
  private boolean match(char expected) {
    if(isAtEnd()) return false;
    if(source.charAt(current) != expected) return false;

    current++;
    return true;
  }

  //lookahead one character without consuming the character
  private char peek() {
    if(isAtEnd()) return '\0';
    return source.charAt(current);
  }

  //lookahead two characters without consumption
  private char peekNext() {
    if(current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  //consume the next character and return it
  private char advance() {
    return source.charAt(current++);
  }

  //grab text for current lexeme and create a new token
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
