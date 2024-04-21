export default class Admin {
    static #showMainMenu

    constructor(callBack) {
        // referenciar la función que restablece el menú principal
        Admin.#showMainMenu = callBack
        // crear el menú del administrador
        createMenu('./resources/html/menus/adminMenu.html', this.#mainMenu)
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
                Admin.#showMainMenu()
                document.querySelector('main').innerHTML = ''
        }
    }
}
