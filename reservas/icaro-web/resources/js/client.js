export default class Client {
    static #showMainMenu

    constructor(callBack) {
        // referenciar la función que restablece el menú principal
        Client.#showMainMenu = callBack
        // crear el menú del cliente
        createMenu('./resources/html/menus/clientMenu.html', Client.#mainMenu)
    }

    async #mainMenu(e) {
        e.preventDefault()
        console.log(e.target.text);

        switch (e.target.text) {
            case 'Aviones':
                // ... falta
                break
            case 'Trayectos':
                // ... falta
                break
            case 'Vuelos':
                const { default: Vuelos } = await import(`./vuelos.js`)
                await Vuelos.init()
                break
            case 'Personal':
                // ... falta
                break
            case 'Salir':
                localStorage.removeItem('user')
                Client.#showMainMenu()
        }
    }
}
