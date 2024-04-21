// version tailwind: https://drive.google.com/drive/folders/1m-RskOpDB1enwETp6sTgksY5BqMhaRHG

export default class Modal {
    #id
    #modal
    #bModal // la instancia de bootstrap.Modal(...)

    constructor({
        size = 'modal-sm', // 'modal-sm' | 'modal-lg' | 'modal-xl'
        style = 'modal-dialog-centered',
        title = '',
        content = '',
        buttons = [], // los botones que se agregan al footer
        built = null // un callBack
    } = {}) {
        const hidden = 'hidden'
        // una de las muchas formas de agregar un elemento al DOM con un id único
        this.#id = Helpers.idRandom('modal-')
        document.querySelector('body').insertAdjacentHTML(
            // hacer caso omiso a la advertencia del flex/hidden
            'beforeend', `
                <div id="${this.#id}" class="modal ${size}" data-bs-backdrop="static" data-bs-keyboard="false">
                    <div class="modal-dialog ${style}" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 id="title-${this.#id}" class="modal-title">Modal title</h5>
                                <button id="close" type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true"></span>
                                </button>
                            </div>
                            <div id="main-${this.#id}" class="modal-body">
                                <p>Modal body text goes here.</p>
                            </div>
                            <footer class="modal-footer"></footer>
                        </div>
                    </div>
                </div>`
        )
        
        this.title = title
        this.content = content
        this.buttons = buttons
        this.#modal = document.querySelector(`#${this.#id}`)
        this.#bModal = new bootstrap.Modal(document.getElementById(this.#id)) 

        if (typeof built === 'function') {
            built(this.#id, this.#bModal)
        }
    }

    get id() {
        return this.#id
    }

    /**
     * Establecer el título del cuadro de diálogo
     * @param {string} _title
     */
    set title(_title) {
        document.querySelector(`#title-${this.#id}`).innerHTML = _title
        return this
    }

    /**
     * Establecer el contenido del cuadro de diálogo
     * @param {string} _content
     */
    set content(_content) {
        document.querySelector(`#main-${this.#id}`).innerHTML = _content
        return this
    }

    /**
     * Agregar botones al pie de página del modal u ocultar el pie de página
     * @param {any[]} _buttons
     */
    set buttons(_buttons) {
        const footer = document.querySelector(`#${this.#id} footer`)
        if (_buttons.length > 0) {
            _buttons.forEach(item => {
                const html = `<button id="${item.id}" type="button" class="${item.style}">${item.html}</button>`
                footer.insertAdjacentHTML('beforeend', html)
                const button = document.querySelector(`#${this.#id} footer #${item.id}`)

                if (button && typeof item.callBack === 'function') {
                    button.addEventListener('click', e => item.callBack(e))
                }
            })
        } else {
            footer.classList.add('hidden')
        }

        // el botón de cerrar de la parte superior derecha del modal
        document.querySelector(`#${this.#id} #close`).addEventListener('click', () => this.close())
    }

    show() {
        if (this.#modal) {
            this.#bModal.show()
        } else {
            console.log('Ya no hay una instancia de modal para ser mostrada');
        }
        return this
    }

    close() {
        this.#bModal.hide()   // ******************************
        return this
    }

    dispose() {
        this.#bModal.hide()
        this.#bModal.dispose()   
        this.#bModal = this.#modal = null;
        document.querySelector(`#${this.#id}`).innerHTML = ''
    }
}