import { ref } from 'vue'

export const isEffectsEnabled = ref(localStorage.getItem('mouseEffects') !== 'false')

// Knowledge Particles Configuration
const PARTICLES = ['📖', '📚', '✏️', '✒️', '🎓', '∑', 'π', '√', '∫', '💡']
const COLORS = ['#10b981', '#3b82f6', '#f59e0b', '#ef4444', '#8b5cf6']

let canvas = null
let ctx = null
let animationFrameId = null
let width = window.innerWidth
let height = window.innerHeight
let particles = []

class Particle {
    constructor(x, y) {
        this.x = x
        this.y = y
        // Random spread
        this.velocity = {
            x: (Math.random() - 0.5) * 3,
            y: (Math.random() - 0.5) * 3 - 2 // Slight upward bias
        }
        this.life = 1.0
        this.decay = 0.01 + Math.random() * 0.02
        this.text = PARTICLES[Math.floor(Math.random() * PARTICLES.length)]
        this.color = COLORS[Math.floor(Math.random() * COLORS.length)]
        this.size = 14 + Math.random() * 10
        this.rotation = Math.random() * 360
        this.rotationSpeed = (Math.random() - 0.5) * 4
    }

    update() {
        this.x += this.velocity.x
        this.y += this.velocity.y
        this.velocity.y += 0.05 // Gravity
        this.rotation += this.rotationSpeed
        this.life -= this.decay
    }

    draw(context) {
        context.save()
        context.translate(this.x, this.y)
        context.rotate((this.rotation * Math.PI) / 180)
        context.globalAlpha = this.life
        context.font = `${this.size}px "Segoe UI Emoji", "Noto Color Emoji", sans-serif`
        context.fillStyle = this.color // Note: Emojis ignore fillStyle usually, but symbols use it
        // Use fillText for symbols/text, assume color is handled by emoji font or fillStyle for simple glyphs
        context.fillText(this.text, -this.size / 2, -this.size / 2)
        context.restore()
    }
}

const onMouseMove = (e) => {
    // Spawn particles on move
    if (Math.random() > 0.7) { // Rate limiter
        particles.push(new Particle(e.clientX, e.clientY))
    }
}

const loop = () => {
    if (!ctx) return

    ctx.clearRect(0, 0, width, height)

    for (let i = particles.length - 1; i >= 0; i--) {
        const p = particles[i]
        p.update()
        p.draw(ctx)

        if (p.life <= 0) {
            particles.splice(i, 1)
        }
    }

    animationFrameId = requestAnimationFrame(loop)
}

const onResize = () => {
    width = window.innerWidth
    height = window.innerHeight
    if (canvas) {
        canvas.width = width
        canvas.height = height
    }
}

const createEffectCanvas = () => {
    // Check if exists
    if (document.getElementById('knowledge-particles')) return

    canvas = document.createElement('canvas')
    canvas.id = 'knowledge-particles'
    // IMPORTANT: Do NOT set data-zr-dom-id so global App.vue styles pick it up (fixed full screen)
    canvas.width = width
    canvas.height = height

    // Fallback inline styles if global CSS misses it (though App.vue should handle it)
    canvas.style.position = 'fixed'
    canvas.style.top = '0'
    canvas.style.left = '0'
    canvas.style.pointerEvents = 'none'
    canvas.style.zIndex = '99999'

    ctx = canvas.getContext('2d')
    document.body.appendChild(canvas)

    window.addEventListener('resize', onResize)
    window.addEventListener('mousemove', onMouseMove)

    loop()
}

const destroyEffectCanvas = () => {
    if (animationFrameId) {
        cancelAnimationFrame(animationFrameId)
        animationFrameId = null
    }

    window.removeEventListener('resize', onResize)
    window.removeEventListener('mousemove', onMouseMove)

    if (canvas) {
        canvas.remove()
        canvas = null
        ctx = null
    }
    particles = []
}

// External Scripts (Heart & Cherry Blossom)
const EXTERNAL_SCRIPTS = [
    'https://cdn.jsdelivr.net/gh/mocchen/cssmeihua/js/yinghua.js',
    'https://cdn.jsdelivr.net/gh/mocchen/cssmeihua/js/aixin.js'
]

const loadExternalScripts = () => {
    EXTERNAL_SCRIPTS.forEach(src => {
        if (document.querySelector(`script[src="${src}"]`)) return

        const script = document.createElement('script')
        script.src = src
        script.defer = true
        document.body.appendChild(script)
    })
}

export const initMouseEffects = () => {
    if (isEffectsEnabled.value) {
        // 1. Start Custom Knowledge Particles
        createEffectCanvas()

        // 2. Load Legacy Effects (Star/Heart/Sakura)
        loadExternalScripts()
    }
}

export const toggleEffects = (enable) => {
    isEffectsEnabled.value = enable
    localStorage.setItem('mouseEffects', enable)

    // Using reload for cleanest toggle of external scripts
    window.location.reload()
}
// Auto init handled by component call to initMouseEffects
