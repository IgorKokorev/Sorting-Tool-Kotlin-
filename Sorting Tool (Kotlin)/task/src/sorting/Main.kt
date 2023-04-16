package sorting

import java.io.File
import java.util.Scanner

var longs = mutableListOf<Long>()
var words = mutableListOf<String>()
val lines = mutableListOf<String>()

enum class InpType { WORD, LINE, LONG}

// default values
var curInpType = InpType.WORD
var isSortNatural = true
var inpFileName = ""
var outFileName = ""

const val ARG_SORT_TYPE = "-sortingType"
const val ARG_DATA_TYPE = "-dataType"
const val INP_FILE = "-inputFile"
const val OUT_FILE = "-outputFile"

fun main(args: Array<String>) {

    if (checkArgs(args)) return

    readLines()
    if (curInpType != InpType.LINE && convertToWords()) {
        println("Empty input!")
        return
    }
    if (curInpType == InpType.LONG && convertToLongs()) {
        println("Empty input!")
        return
    }

    if (isSortNatural)
        when (curInpType) {
            InpType.WORD ->
                printNatural(sortList(words), "words", " ")
            InpType.LINE ->
                printNatural(sortList(lines), "lines", "\n")
            InpType.LONG ->
                printNatural(sortList(longs), "numbers", " ")
        }
    else
        when (curInpType) {
            InpType.WORD -> printByCount(sortByCount(words), "words", words.size)
            InpType.LINE -> printByCount(sortByCount(lines), "lines", lines.size)
            InpType.LONG -> printByCount(sortByCount(longs), "numbers", longs.size)
        }
}

private fun checkArgs(args: Array<String>): Boolean {

    val usedArgs = mutableListOf<String>()

    if (args.isNotEmpty()) {
        if (args.contains(INP_FILE)) {
            val i = args.indexOf(INP_FILE)
            usedArgs.add(args[i])
            if (i < args.lastIndex) {
                inpFileName = args[i + 1]
                usedArgs.add(args[i + 1])
            } else {
                println("No input file defined!")
                return true
            }
        }

        if (args.contains(OUT_FILE)) {
            val i = args.indexOf(OUT_FILE)
            usedArgs.add(args[i])
            if (i < args.lastIndex) {
                outFileName = args[i + 1]
                usedArgs.add(args[i + 1])
            } else {
                println("No output file defined!")
                return true
            }
        }

        if (args.contains(ARG_SORT_TYPE)) {
            val i = args.indexOf(ARG_SORT_TYPE)
            usedArgs.add(args[i])
            if (i < args.lastIndex) {
                if (args[i + 1].equals("byCount")) {
                    isSortNatural = false
                    usedArgs.add(args[i + 1])
                } else if (args[i + 1].equals("natural")) {
                    isSortNatural = true
                    usedArgs.add(args[i + 1])
                } else {
                    println("No sorting type defined!")
                    return true
                }
            } else {
                println("No sorting type defined!")
                return true
            }
        }

        if (args.contains(ARG_DATA_TYPE)) {
            val i = args.indexOf(ARG_DATA_TYPE)
            usedArgs.add(args[i])
            if (i < args.lastIndex) {
                if (args[i + 1].equals("long")) {
                    curInpType = InpType.LONG
                    usedArgs.add(args[i + 1])
                } else if (args[i + 1].equals("line")) {
                    curInpType = InpType.LINE
                    usedArgs.add(args[i + 1])
                } else if (args[i + 1].equals("word")) {
                    curInpType = InpType.WORD
                    usedArgs.add(args[i + 1])
                } else {
                    println("No data type defined!")
                    return true
                }
            } else {
                println("No data type defined!")
                return true
            }
        }
    }

    for (arg in args)
        if (!usedArgs.contains(arg))
            println("\"$arg\" is not a valid parameter. It will be skipped.")
    return false
}

data class ElemNumber<T>(var elem: T, var int: Int)

fun <T> printByCount(list: MutableList<ElemNumber<T>>, s: String, numElem: Int) {

    writeTo("Total $s: $numElem.\n")

    for (elem in list)
        writeTo("${elem.elem}: ${elem.int} time(s), ${elem.int * 100 / numElem}%\n")
}

fun <T> sortByCount(list: MutableList<T>): MutableList<ElemNumber<T>> {
    val map = list.groupingBy { it }.eachCount()
    val res: MutableList<ElemNumber<T>> = mutableListOf()
    for (elem in map) res.add(ElemNumber(elem.key, elem.value))

    return sortList(res)
}

fun readLines(): Boolean {

    val scanner = if (inpFileName.isEmpty()) {
        Scanner(System.`in`)
    } else Scanner(File(inpFileName))

    // reading lines
    while (scanner.hasNext()) {
        lines.add(scanner.nextLine())
    }
    scanner.close()
    return lines.isEmpty()
}

fun convertToWords(): Boolean {

    for (line in lines){
        val inpWords = line.trim().split(Regex("\\s+"))
        for (word in inpWords) {
            words.add(word)
        }
    }

    return words.isEmpty()
}

private fun convertToLongs(): Boolean {
    for (word in words) {
        val num = try {
            word.toLong()
        } catch (e: NumberFormatException) {
            println("\"$word\" is not a long. It will be skipped.")
            continue
        }
        longs.add(num)
    }
    return longs.isEmpty()
}

fun <T> printNatural(list: MutableList<T>, elemName: String, delim: String) {

    writeTo("Total $elemName: ${list.size}.\n")
    writeTo("Sorted data:")
    for (l in list) writeTo("$delim$l")
    writeTo("\n")
}

fun writeTo(s: String) {
    if (outFileName.isEmpty()) print(s)
    else File(outFileName).writeText(s)
}

fun <T> sortList(list: MutableList<T>): MutableList<T> {
    return when (list.size) {
        1 -> list
        2 -> {
            if (isMore(list[0], list[1])){
                val l = list[0]
                list[0] = list[1]
                list[1] = l
            }
            list
        }
        else -> {
            val list1 = sortList(list.subList(0, list.size / 2))
            val list2 = sortList(list.subList(list.size / 2, list.size))
            mergeLists(list1, list2)
        }
    }
}

fun <T> isMore(arg1: T, arg2: T): Boolean {
    if (arg1 is Long && arg2 is Long) return arg1 > arg2
    if (arg1 is String && arg2 is String)
        return arg1.compareTo(arg2) > 0
    if (arg1 is ElemNumber<*> && arg2 is ElemNumber<*>) {
        if (arg1.int > arg2.int) return true
        else if (arg1.int == arg2.int) return isMore(arg1.elem, arg2.elem)
    }
    return false
}

fun <T> mergeLists(list1: MutableList<T>, list2: MutableList<T>): MutableList<T> {
    var i1 = 0
    var i2 = 0
    val res = mutableListOf<T>()
    while (i1 < list1.size || i2 < list2.size) {
        if (i1 == list1.size) res.add(list2[i2++])
        else if (i2 == list2.size) res.add(list1[i1++])
        else if (isMore(list1[i1], list2[i2])) res.add(list2[i2++])
        else res.add(list1[i1++])
    }
    return res
}

