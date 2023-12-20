package lab2Arch

interface IPort {
    fun incomingShip(s: Ship)
    fun outgoingShip(s: Ship)
}