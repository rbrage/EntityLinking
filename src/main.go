package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

func main() {

	path := ""

	if err := os.Args; err == nil {
		if len(os.Args) < 2 {
			fmt.Println("You need to send in some values as arguments to use this program.")
		}
		path = os.Args[1]
	} else {
		path = "/home/rbrage/Program/go/text.txt"
	}
	fmt.Printf("Starts reading the file.\n")
	ReadFromFile(path)
	fmt.Printf("Done reading the file.\n")
}

func ReadFromFile(path string) {
	file, fileerr := os.Open(path)
	if fileerr != nil {
		fmt.Println(fileerr)
	}

	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanWords)

	wordsUpperCase := []string{}
	wordsLenFour := []string{}

	for scanner.Scan() {
		word := scanner.Text()
		if len(word) >= 4 {
			wordsLenFour = append(wordsLenFour, word+"|")
		}

		character := string(word[0])
		if _, err := strconv.Atoi(word); err != nil {
			if (character == strings.ToUpper(character)) && (len(word) > 1) {
				wordsUpperCase = append(wordsUpperCase, word+"|")
			}
		}

	}

	if err := scanner.Err(); err != nil {
		fmt.Fprintln(os.Stderr, "reading file:", err)
	}
	fmt.Printf("Word length: %d \nCapitilized wordcount: %d \n", len(wordsLenFour), len(wordsUpperCase))
	fmt.Println(wordsUpperCase)
	fmt.Println(wordsLenFour)
}
