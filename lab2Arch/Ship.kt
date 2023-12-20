package lab2Arch

class Ship(
    val id: Int,
    var fuel: Double,
    var currentPort: Port,
    val totalWeightCapacity: Int,
    val maxNumberOfAllContainers: Int,
    val maxNumberOfBasicContainers: Int,
    val maxNumberOfHeavyContainers: Int,
    val maxNumberOfRefrigeratedContainers: Int,
    val maxNumberOfLiquidContainers: Int,
    val fuelConsumptionPerKM: Double
) : IShip {
    private val containers = mutableListOf<Container>()

    fun getCurrentContainers(): List<Container> = containers.sortedBy { container -> container.id }

    override fun sailTo(p: Port): Boolean {
        val distance = currentPort.getDistance(p)
        val totalFuelConsumptionPerKM = (fuelConsumptionPerKM + containersFuelConsumption() / distance)
            .coerceAtLeast(0.0)

        val requiredFuel = totalFuelConsumptionPerKM * distance
        val canSail = fuel >= requiredFuel
        if (canSail) fuel -= requiredFuel
        notifyPorts(p)

        return canSail
    }

    private fun notifyPorts(p: Port) {
        currentPort.outgoingShip(s = this)
        p.incomingShip(s = this)
    }

    fun changeCurrentPort(p: Port) { currentPort = p }

    private fun totalContainersWeight() = containers.sumOf { container -> container.weight }

    private fun containersFuelConsumption() = containers.sumOf { container -> container.consumption() }
 
     override fun unLoad(cont: Container): Boolean {
        return if (containers.contains(cont)) {
            currentPort.containersList.add(cont)
            containers.remove(cont)
        } else false
    }

    override fun reFuel(newFuel: Double) {
        fuel += newFuel
    }

    private fun canLoadContainerByType(cont: Container): Boolean {
        return when (cont) {
            is BasicContainer -> containers.count { container -> container is BasicContainer } <= maxNumberOfBasicContainers
            is LiquidContainer -> containers.count { container -> container is LiquidContainer } <= maxNumberOfLiquidContainers
            is RefrigeratedContainer -> containers.count { container -> container is RefrigeratedContainer } <= maxNumberOfRefrigeratedContainers
            is HeavyContainer -> containers.count { container -> container is HeavyContainer } <= maxNumberOfHeavyContainers
            else -> false
        }
    }

    override fun load(cont: Container): Boolean {
        return if (canLoadContainerByType(cont) &&
            totalContainersWeight() <= totalWeightCapacity &&
            containers.size <= maxNumberOfAllContainers &&
            currentPort.containersList.remove(cont)
        ) {
            containers.add(cont)
            true
        } else false
    }

   
}
