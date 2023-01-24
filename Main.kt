package tasklist

import kotlinx.datetime.*
import java.time.LocalTime
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

class Task(id: Int, subTasks: MutableList<String>) {
    var id: Int = id
    var subTasks:MutableList<String> = subTasks
}

var index = 0
fun main() {
    val taskMap = readTasks()
    var action = menuAction()
    while (true) {
        when (action) {
            "add" -> {
                val priority = menuInputTaskPriority()
                val date = menuInputDate()
                val time = menuInputTime()
                val tag = calculateTaskTag(date)
                val dateBuilt = "$date $time $priority $tag"
                menuInputTask(taskMap, dateBuilt)
                action = menuAction()
            }
            "print" -> {
                menuActionPrint(taskMap)
                action = menuAction()
            }
            "end" -> {
                menuActionExit()
                writeTaks(taskMap)
                break
            }
            "delete" -> {
                menuActionDeleteTask(taskMap)
                action = menuAction()
            }
            "edit" -> {
                menuActionEditTask(taskMap)
                action = menuAction()
            }
            else -> {
                println("The input action is invalid")
                action = menuAction()
            }
        }
    }


}

fun readTasks(): MutableMap<Int, MutableList<String>> {
    val jsonFile = File("tasklist.json")
    if (jsonFile.exists()) {

        val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val taskAdapter = moshi.adapter<MutableList<Task>>(type)

        val taskMap = mutableMapOf<Int, MutableList<String>>()

        taskAdapter.fromJson(jsonFile.readText())?.forEach { t ->
            run {
                if (t != null) {
                    taskMap[t.id] = t.subTasks.toMutableList()
                }
            }
        }

        index = taskMap.size
        return taskMap

    } else {
        return mutableMapOf()
    }
}

fun writeTaks(taskMap: MutableMap<Int, MutableList<String>>) {

    val jsonFile = File("tasklist.json")
    if (jsonFile.exists()) {
        jsonFile.delete()
    }
    jsonFile.createNewFile()
    val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val taskAdapter = moshi.adapter<MutableList<Task?>>(type)

    val mutableListTask = mutableListOf<Task?>()

    taskMap.forEach { (t, u) -> mutableListTask.add(Task(t, u)) }

    jsonFile.appendText(taskAdapter.toJson(mutableListTask))

}

fun menuAction(): String {
    println("Input an action (add, print, edit, delete, end):")
    return readln().replace("> ", "").replace(">", "")
}

fun menuInputTask(taskMap: MutableMap<Int, MutableList<String>>, menuInputTaskPriority: String) {
    val taskList = mutableListOf<String>()
    val taskList2 = mutableListOf<String>()
    println("Input a new task (enter a blank line to end):")
    var line = readln().replace("> ", "").replace(">", "").trim()
    while (line != "") {
        val lbn = line.trim()
        if (lbn != "") {
            taskList.add(line.trim())
        } else {
            break
        }
        line = readln().replace("> ", "").replace(">", "")
    }
    if(taskList.isNotEmpty()){
        taskList2.add(menuInputTaskPriority)
        taskList.forEach{
            taskList2.add(it)
        }
        taskMap[index] = taskList2
        index++
    }
    else{
        println("The task is blank")
    }
}

fun menuInputTask(taskMap: MutableMap<Int, MutableList<String>>, headerTasks: String, selectedIndex: Int) {
    val taskList = mutableListOf<String>()
    val taskList2 = mutableListOf<String>()
    println("Input a new task (enter a blank line to end):")
    var line = readln().replace("> ", "").replace(">", "").trim()
    while (line != "") {
        val lbn = line.trim()
        if (lbn != "") {
            taskList.add(line.trim())
        } else {
            break
        }
        line = readln().replace("> ", "").replace(">", "")
    }
    if(taskList.isNotEmpty()){
        taskList2.add(headerTasks)
        taskList.forEach{
            taskList2.add(it)
        }
        taskMap[selectedIndex] = taskList2
    }
    else{
        println("The task is blank")
    }
}

fun menuInputTaskPriority(): String {
    while (true) {
        println("Input the task priority (C, H, N, L):")
        val tpriority = readln().replace("> ", "").replace(">", "")
        if(tpriority.lowercase() == "c" || tpriority.lowercase() == "h" || tpriority.lowercase() == "n" || tpriority.lowercase() == "l"){
            return tpriority.uppercase()
        }
    }
}

fun menuInputDate(): String {
    while (true) {
        println("Input the date (yyyy-mm-dd):")
        try {
            val inputDate = readln()
                .replace("> ", "")
                .replace(">", "")
                .split("-")
                .map { it.toInt() }
            val date = LocalDate(inputDate[0], inputDate[1], inputDate[2])
            return date.toString()
        } catch (e: Exception) {
            println("The input date is invalid")
            continue
        }
    }
}

fun menuInputTime(): String {
    while(true){
        try {
            println("Input the time (hh:mm):")
            val inputTime = readln()
                .replace("> ", "")
                .replace(">", "")
                .split(":")
                .map { it.toInt() }
            val time = LocalTime.of(inputTime[0], inputTime[1])
            return time.toString()
        }
        catch (e: Exception){
            println("The input time is invalid")
        }
    }
}

fun menuActionPrint(taskMap: MutableMap<Int, MutableList<String>>) {
    if (taskMap.isEmpty()) {
        println("No tasks have been input")
    } else {
        println("+----+------------+-------+---+---+--------------------------------------------+")
        println("| N  |    Date    | Time  | P | D |                   Task                     |")
        println("+----+------------+-------+---+---+--------------------------------------------+")
        taskMap.forEach {
            val customIndex = it.key + 1
            menuActionPrintTaskList(it.value, customIndex)
            if(it.key != taskMap.size-1){
                println("+----+------------+-------+---+---+--------------------------------------------+")
            }
        }
        println("+----+------------+-------+---+---+--------------------------------------------+")
    }
}

fun menuActionPrintTaskList(tasklist: MutableList<String>, index: Int) {
    val priorityColorMap = mutableMapOf<Char, String>()
    priorityColorMap['C'] = "\u001B[101m \u001B[0m"
    priorityColorMap['H'] = "\u001B[103m \u001B[0m"
    priorityColorMap['N'] = "\u001B[102m \u001B[0m"
    priorityColorMap['L'] = "\u001B[104m \u001B[0m"

    val tagColorMap = mutableMapOf<Char, String>()
    tagColorMap['I'] = "\u001B[102m \u001B[0m"
    tagColorMap['T'] = "\u001B[103m \u001B[0m"
    tagColorMap['O'] = "\u001B[101m \u001B[0m"

    var c = 0

    tasklist.forEach{
        if(c == 0){
            val date = it.subSequence(0,10)
            val time = it.subSequence(11,16)
            val priority = it[17]
            val tag = it[it.length-1]
            print("| $index  | $date | $time | ${priorityColorMap.getValue(priority)} | ${tagColorMap.getValue(tag)} ")
        }
        else{
            menuActionPrintTaskListWrapped(it, c)
        }
        c++
    }
}

fun menuActionPrintTaskListWrapped(line: String, index: Int){
    val maxLineChars = 44
    val linesOfTask = mutableListOf<String>()

    if(line.length <= maxLineChars){
        linesOfTask.add(line)
    }
    else{
        var numberOfLines = line.length / maxLineChars
        if(line.length % maxLineChars == 0){
            numberOfLines--
        }
        var minIter = 0
        var maxIter = 44
        var c = 0
        for (i in 0 .. numberOfLines){
            if(c == numberOfLines){
                linesOfTask.add(line.substring(minIter, line.length))
                c++
            }
            else{
                linesOfTask.add(line.substring(minIter, maxIter))
                minIter+=maxLineChars
                maxIter+=maxLineChars
                c++
            }
        }
    }
    var c = 0
    linesOfTask.forEach {
            lineWrapped ->
        run {
            if (c == 0 && index == 1) {
                println("|${lineWrapped.padEnd(maxLineChars)}|")
                c++
            } else {
                println("|    |            |       |   |   |${lineWrapped.padEnd(maxLineChars)}|")
                c++
            }
        }
    }
}

fun menuActionExit() {
    println("Tasklist exiting!")
}

fun calculateTaskTag(taskDate: String): String {
    val taskDateObj = LocalDate.parse(taskDate)
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
    return if(taskDateObj == currentDate){
        "T"
    }
    else if(currentDate.daysUntil(taskDateObj) > 0){
        "I"
    }
    else{
        "O"
    }
}

fun menuActionDeleteTask(taskMap: MutableMap<Int, MutableList<String>>){
    if (taskMap.isEmpty()) {
        println("No tasks have been input")
    }
    else{
        menuActionPrint(taskMap)
        while (true) {
            println("Input the task number (1-${taskMap.size}):")
            try {
                val indexToRemove = readln().replace("> ", "").replace(">", "").toInt()
                if (indexToRemove >= 1 && indexToRemove <= taskMap.size) {
                    val taskMapV2 = mutableMapOf<Int, MutableList<String>>()
                    var counter = 0
                    if (indexToRemove != taskMap.size) {
                        taskMap.remove(indexToRemove - 1)
                        taskMap.forEach { (_, value) ->
                            taskMapV2[counter] = value
                            counter++
                        }
                        taskMap.remove(taskMap.size - 1)
                        taskMap.clear()
                        taskMapV2.forEach{
                                (key,value) ->
                            taskMap[key] = value
                        }
                    }
                    else{
                        taskMap.remove(indexToRemove - 1)
                    }
                    index--
                    println("The task is deleted")
                    break
                } else {
                    println("Invalid task number")
                }
            }
            catch (e: Exception){
                println("Invalid task number")
                continue
            }

        }
    }
}

fun menuActionEditTask(taskMap: MutableMap<Int, MutableList<String>>){
    if (taskMap.isEmpty()) {
        println("No tasks have been input")
    }
    else{
        menuActionPrint(taskMap)
        while(true){
            println("Input the task number (1-${taskMap.size}):")
            try {
                val indexToEdit = readln().replace("> ", "").replace(">", "").toInt()
                if(indexToEdit >= 1 && indexToEdit <= taskMap.size){
                    while(true){
                        println("Input a field to edit (priority, date, time, task):")
                        val field = readln().replace("> ", "").replace(">", "").trim()
                        if(field == "priority"){
                            val priority = menuInputTaskPriority()
                            val listValues = taskMap[indexToEdit-1]
                            if (listValues != null) {
                                val value = listValues[0].replaceRange(17,18, priority)
                                listValues[0] = value
                                taskMap[indexToEdit-1] = listValues
                            }
                            println("The task is changed")
                            break
                        }
                        else if(field == "date"){
                            val date = menuInputDate()
                            val listValues = taskMap[indexToEdit-1]
                            if (listValues != null) {
                                val value = listValues[0].replaceRange(0,10, date)
                                listValues[0] = value
                                taskMap[indexToEdit-1] = listValues
                            }
                            println("The task is changed")
                            break
                        }
                        else if(field == "time"){
                            val time = menuInputTime()
                            val listValues = taskMap[indexToEdit-1]
                            if (listValues != null) {
                                val value = listValues[0].replaceRange(11,16, time)
                                listValues[0] = value
                                taskMap[indexToEdit-1] = listValues
                            }
                            println("The task is changed")
                            break
                        }
                        else if(field == "task"){
                            val listValues = taskMap[indexToEdit-1]
                            if (listValues != null) {
                                taskMap[indexToEdit-1]?.let { menuInputTask(taskMap, it[0], indexToEdit-1) }
                            }
                            println("The task is changed")
                            break
                        }
                        else{
                            println("Invalid field")
                        }
                    }
                    break
                }
                else{
                    println("Invalid task number")
                }
            }
            catch (e: Exception){
                println("Invalid task number")
                continue
            }

        }
    }
}


