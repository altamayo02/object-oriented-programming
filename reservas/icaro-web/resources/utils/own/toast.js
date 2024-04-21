
export default class Toast {

    #instance

    /**
     * Crea una instancia de Toast utilizable en toda la aplicación
     */
    constructor() {
        const htmlToast = `
            <div id="on-top" class="position-fixed top-0 end-0 p-3">
                <div id="toast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                    <div class="toast-header">
                        <i id="icon"></i>
                        <strong class="me-auto">Bootstrap</strong>
                    </div>
                    <div class="toast-body">
                        Hello, world! This is a toast message.
                    </div>
                </div>
            </div>`

        const contentToast = document.querySelector('body').insertAdjacentHTML('afterbegin', htmlToast)
        this.#instance = new bootstrap.Toast('#toast', { delay: 3000 })
    }

    get instance() {
        return this.#instance
    }

    /**
     * Estándar para la presentaciones de errores, advertencias o información general
     * @param {String} message El mensaje que se debe mostrar
     * @param {String} mode Indica el tipo de toast a mostrar: 'success', 'warning', 'danger' o 'info'
     * @param {string} error Preferiblemente aquellos mensajes reportados por el entorno de ejecución
     */
    info = ({ message = '', mode = 'info', error = '' } = {}) => {
        mode = mode.toLowerCase()
        let background, colour, title, icon

        if (mode === 'success') {
            title = 'Acción exitosa'
            background = 'bg-success'
            colour = 'text-light'
            icon = '<i class="bi bi-check-square-fill me-2"></i>'
        } else if (mode === 'warning') {
            title = '¡Cuidado!'
            colour = 'text-body'
            background = 'bg-warning'
            icon = '<i class="bi bi-exclamation-square-fill text-dark me-2"></i>'
        } else if (mode === 'danger') {
            title = 'Problemas...'
            background = 'bg-danger'
            colour = 'text-light'
            icon = '<i class="bi bi-x-octagon-fill text-danger me-2"></i>'
        } else {
            title = 'Icaro Air'
            background = 'bg-info'
            colour = 'text-light'
            icon = '<i class="bi bi-info-square-fill me-2"></i>'
        }

        if (error) {
            console.error(error)
        }

        document.querySelector(`#toast #icon`).innerHTML = icon
        document.querySelector(`#toast .me-auto`).innerHTML = title
        document.querySelector(`#toast .toast-body`).innerHTML = message

        const elemento = document.querySelector('#toast')
        elemento.classList.remove('bg-success', 'bg-warning', 'bg-danger', 'bg-info')
        elemento.classList.add(background, colour)
        
        document.querySelector('#on-top').style.zIndex = "1100"
        this.instance.show()

    }

}