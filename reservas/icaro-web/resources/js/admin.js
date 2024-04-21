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
                const { default: Aviones } = await import(`./aviones.js`)
                await Aviones.init()
                break
            case 'Trayectos':
                const { default: Trayectos } = await import(`./trayectos.js`)
                await Trayectos.init()
                break
            case 'Vuelos':
                const { default: Vuelos } = await import(`./vuelos.js`)
                await Vuelos.init()
                break
            case 'Gestión de usuarios':
                const { default: Usuarios } = await import(`./usuarios.js`)
                await Usuarios.init()
                break
            case 'Salir':
                localStorage.removeItem('user')
                Admin.#showMainMenu()
        }
    }
}
