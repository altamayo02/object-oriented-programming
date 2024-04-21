import * as Popper from '../utils/popper/popper.min.js'
import * as bootstrap from '../utils/bootstrap/bootstrap.min.js'
import { TabulatorFull as Tabulator } from '../utils/tabulator/js/tabulator_esm.min.js'
import { DateTime, Duration } from '../utils/luxon/luxon.min.js'
import icons from '../utils/own/icons.js'
import Helpers from '../utils/own/helpers.js'
import Modal from '../utils/own/modal.js'

let current = null // opción actual

class App {
    static #user
    static #frmLogin

    static async main() {
        // Las clases importadas se asignan a referencias de la ventana actual:
        // Ver: https://www.digitallearning.es/curso-javascript-jquery-objeto-window-propiedades.html
        window.DateTime = DateTime
        window.Duration = Duration
        window.Helpers = Helpers
        window.Tabulator = Tabulator
        window.Modal = Modal
        window.icons = icons

        const config = await Helpers.fetchData('./resources/assets/config.json')
        window.urlAPI = config.url

        const { default: Toast } = await import(`../utils/own/toast.js`)
        window.Toast = new Toast()

        // crear el cuadro de diálogo que permite autenticarse
        App.#frmLogin = new Modal({
            title: 'Ingreso de usuarios',
            content: await Helpers.loadPage('./resources/html/login.html'),
            buttons: [
                {
                    id: 'ingresar',
                    style: 'btn btn-primary',
                    html: `${icons.doorOpen}<span> Ingresar</span>`,
                    callBack: App.#checkUser
                },
                {
                    id: 'cancel',
                    style: 'btn btn-secondary',
                    html: `${icons.personAdd}<span> Registrarse</span>`,
                    callBack: () => App.#frmLogin.dispose()
                }
            ]
        }).show()

        // crear a nivel de window una función para generar el menú 
        window.createMenu = async (menu, callBack) => {
            // inyecta en la página principal el menú que se recibe como argumento
            document.querySelector('#main-menu').innerHTML = await Helpers.loadPage(menu)
            // referencia la listax de opciones del menú cargado
            const listOptions = document.querySelectorAll('#main-menu a')
            // asigna los gestores de eventos clic para las opciones
            listOptions.forEach(item => item.addEventListener('click', callBack))
        }

        App.#showMainMenu()
    }

    static #showMainMenu() {
        // usa la función para generar el menú principal de la aplicación
        createMenu('./resources/html/menus/mainMenu.html', App.#mainMenu)
    }

    /**
     * Permite autenticarse como usuario del sistema. Si la autenticación es exitosa
     * delega a loadUserPage() la carga de las opciones del usuario
     */
    static async #checkUser() {
        try {
            // la petición POST documentada en el taller 4, punto 23 y taller 5, puntos 44 y 45
            let response = await Helpers.fetchData(`${urlAPI}/usuarios/autenticar`, {
                method: 'POST',
                body: {
                    identificacion: document.querySelector('#user-id').value,
                    password: document.querySelector('#password').value
                }
            })

            if (response.message === 'ok') {
                App.#user = response.data
                localStorage.setItem('user', JSON.stringify(App.#user))
                await App.#loadUserPage()
                App.#frmLogin.dispose()
            } else {
                Toast.info({ message: response.message, mode: 'danger', error: response })
            }
        } catch (error) {
            Toast.info({ message: 'Falló la autenticación del usuario', mode: 'danger', error })
        }
    }

    /**
     * Carga una página de opciones según el usuario autenticado
     * Importante: se usa la carga dinámica de módulos JS:
     * Ver: https://blog.webdevsimplified.com/2021-03/dynamic-module-imports/
     */
    static async #loadUserPage() {
        Toast.info({
            message: `Hola ${App.#user.nombres}`
        })

        switch (App.#user.perfil) {
            case 'ADMINISTRADOR':
                const { default: Admin } = await import(`./admin.js`)
                current = new Admin(App.#showMainMenu)
                break
            case 'CLIENTE':
                // proceda de forma similar al caso para ADMINISTRADOR
                // ...
                break
            case 'AUXILIAR':
                // proceda de forma similar al caso para ADMINISTRADOR
                // ...
                break
            default:
                break
        }
    }

    /**
     * Determina la acción a llevar a cabo según la opción elegida en el menú principal
     * @param {String} option El texto del ancla seleccionada
     */
    static async #mainMenu(e) {
        e.preventDefault()

        let option = ''
        if (e !== undefined) {
            option = e.target.text
        }

        switch (option) {
            case 'Promociones':
                console.log('Falta implementar: ' + option)
                break
            case 'Acerca de...':
                console.log('Falta implementar: ' + option)
                break
            case 'Ingresar':
                console.log('Falta implementar: ' + option)
                break
            case 'Registrarse':
                console.log('Falta implementar: ' + option)
                break
            case 'Tema':
                console.log('Falta implementar: ' + option)
                break
        }
    }
}

;(async () => await App.main())()
