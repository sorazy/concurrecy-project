Names   : Soliman Alnaizy, Ryan Dozier, Natasha Zdravkovic
Semester: Spring 2019
Project : Final presentation for COP4520. Unbounded Lockfree Deque

THE SCIENCE:
    Read the PDF :)

HOW TO RUN:
    1) Thread safe Lockfree Deque:
      - Open the directory of the downloaded file in a Linux based terminal.
      - Go to the "Concorruent Unbounded Deque" file.
      - Compile the Main.java file by using the command:
          "javac Main.java"
      - Run the Main class using the command:
          "java Main"
          
    2) STM version of the deque:
      - Open the directory of the downloaded file in a Linux based terminal.
      - We provided a test case that that will perform 10,000 operations of transaction sizes of 1.
      - The testcase will randomly call push and pop methods. It will have a 25% chance of calling 
        push_left, 25% chance of calling pop_left(), a 25% chance of calling push_right(), and 
        finally a 25% chance of calling pop_right().
      - Run the following command to compile and run the test case:
          "java -cp STMDeque.jar com.mycompany.app.App"

    3) The improved version of the STM deque:
        - Open the directory of the downloaded file in a Linux based terminal.
        - We provided a test case that that will perform 10,000 operations of transaction sizes of 1.
        - The testcase will randomly call push and pop methods. It will have a 25% chance of calling 
          push_left, 25% chance of calling pop_left(), a 25% chance of calling push_right(), and 
          finally a 25% chance of calling pop_right().
        - Run the following command to compile and run the test case:
            "java -cp ImprovedSTMDeque.jar com.mycompany.app.App"
