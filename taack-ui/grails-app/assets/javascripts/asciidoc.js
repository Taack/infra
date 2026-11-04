function emptyColumnBefore(element) {
    const column = element.closest('[class*="col-"]')
    const previous = column?.previousElementSibling
    if (previous && previous.matches('[class*="col-"]') && previous.textContent.trim() === '') return previous
    return null
}

function highlightCurrentSection(tocHolder, content) {
    const links = new Map()
    for (const link of tocHolder.querySelectorAll('a[href^="#"]')) {
        links.set(decodeURIComponent(link.getAttribute('href').substring(1)), link)
    }
    const headings = Array.from(content.querySelectorAll('h1[id], h2[id], h3[id], h4[id]')).filter(h => links.has(h.id))
    if (headings.length === 0) return

    let current = null
    let pending = false
    const update = () => {
        // The current section is the last one whose heading reached the spot an anchor jump scrolls it to.
        // A looser limit would pick the next heading whenever the section jumped to is short.
        const readingMargin = 24
        let heading = headings[0]
        for (const h of headings) {
            const anchorTop = parseFloat(getComputedStyle(h).scrollMarginTop) || 0
            if (h.getBoundingClientRect().top > anchorTop + readingMargin) break
            heading = h
        }
        const link = links.get(heading.id)
        if (link === current) return
        current?.classList.remove('active')
        link.classList.add('active')
        current = link
        if (link.offsetTop < tocHolder.scrollTop || link.offsetTop > tocHolder.scrollTop + tocHolder.clientHeight - link.offsetHeight) {
            tocHolder.scrollTop = link.offsetTop - tocHolder.clientHeight / 2
        }
    }
    // A scroll listener rather than an IntersectionObserver: a fast scroll or an anchor jump moves headings
    // past the observed band in one frame without any intersection change. Capture also catches inner scroll containers.
    document.addEventListener('scroll', () => {
        if (pending) return
        pending = true
        requestAnimationFrame(() => {
            pending = false
            if (content.isConnected) update()
        })
    }, {capture: true, passive: true})
    update()
}

function enableImageZoom(content) {
    for (const image of content.querySelectorAll('.imageblock img')) {
        image.addEventListener('click', () => {
            const overlay = document.createElement('div')
            overlay.className = 'asciidocZoom'
            const zoomed = image.cloneNode()
            zoomed.removeAttribute('width')
            zoomed.removeAttribute('height')
            overlay.appendChild(zoomed)
            const close = () => {
                overlay.remove()
                document.removeEventListener('keydown', onKey)
            }
            const onKey = (e) => {
                if (e.key === 'Escape') close()
            }
            overlay.addEventListener('click', close)
            document.addEventListener('keydown', onKey)
            document.body.appendChild(overlay)
        })
    }
}

function manageAsciidoc() {
    for (const asciidocMain of document.getElementsByClassName('asciidocMain')) {
        if (asciidocMain.dataset.asciidocManaged) continue
        asciidocMain.dataset.asciidocManaged = 'true'
        enableImageZoom(asciidocMain)

        const toc = asciidocMain.querySelector('#toc')
        if (!toc) continue
        const tocColumn = emptyColumnBefore(asciidocMain)
        if (tocColumn) {
            const tocHolder = document.createElement('nav')
            tocHolder.className = 'asciidocToc'
            toc.classList.remove('toc2')
            tocHolder.appendChild(toc)
            tocColumn.appendChild(tocHolder)
            highlightCurrentSection(tocHolder, asciidocMain)
        } else {
            toc.classList.add('toc2')
        }
    }
}

manageAsciidoc()
