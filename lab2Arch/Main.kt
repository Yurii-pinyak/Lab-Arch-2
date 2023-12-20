fun main() {
    val (gson, jsonObject) = loadJsonAndCreateGsonMap()

    val containers = getContainers(jsonObject, gson)
    val ports = containers?.let { getPorts(jsonObject, gson, it) }
    val ship = ports?.let { getShips(jsonObject, gson, it) }

    val outputPorts = mutableListOf<PortData>()
    val outputShips = mutableListOf<ShipData>()

    if (ship != null) {
        loadAndExecuteActionsFromJson(jsonObject, gson, ship, containers, ports, outputPorts, outputShips)
    }
    createJsonBasedOnActions(gson, outputPorts, outputShips)
}

private fun loadJsonAndCreateGsonMap(): Pair<Gson, Map<*, *>> {
    val jsonString = File("src/main/kotlin/lab2/Input.json").readText()
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonString, Map::class.java)
    return Pair(gson, jsonObject)
}

private fun createJsonBasedOnActions(
    gson: Gson,
    outputPortData: List<PortData>,
    outputShipsData: List<ShipData>
) {
    val portsAndShipsMap = mapOf(
        "ports" to outputPortData,
        "ships" to outputShipsData
    )
    val portsAndShipsResultsJSON = gson.toJson(portsAndShipsMap)
    File("src/main/kotlin/lab2/Output.json").writeText(portsAndShipsResultsJSON)
}

private fun loadAndExecuteActionsFromJson(
    jsonObject: Map<*, *>,
    gson: Gson,
    ships: List<Ship>,
    containers: List<Container>,
    ports: List<Port>,
    outputPortData: MutableList<PortData>,
    outputShipData: MutableList<ShipData>
) {
    try {
        (jsonObject["actions"] as List<*>).map { action ->
            val actionData = gson.fromJson(gson.toJson(action), ActionData::class.java)
            val ship = actionData.ship

            when (actionData.action) {
                "load" -> executeLoadAction(actionData, ships[ship], containers)
                "unload" -> executeUnloadAction(actionData, ships[ship], containers)
                "sail" -> executeSailAction(ships[ship], ports, actionData)
                "refuel" -> executeRefuelAction(ships[ship], actionData)
                else -> println("[Error] Invalid action in Input.json/\"actions\". " +
                        "Must be one of the following: load, unload, sail, refuel. Please check your json file and try again.")
            }
        }
        convertPortToPortData(ports, outputPortData)
        convertShipToShipData(ships, outputShipData)
    } catch (e: JsonSyntaxException) {
        println("[Error] Unable to perform the action. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types.")
    }
}

