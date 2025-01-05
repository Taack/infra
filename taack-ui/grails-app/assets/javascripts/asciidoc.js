function manageAsciidoc() {
    const asciidocMains = document.getElementsByClassName("asciidocMain")


    for (let i = 0; i < asciidocMains.length; i++) {
        const asciidocMain = asciidocMains[i];

        let divModal = asciidocMain.parentElement;
        const navbars = asciidocMain.getElementsByClassName("toc");
        if (!navbars) return;
        const navbar = navbars[0]
        navbar.classList.add("toc2")
        window.onresize = function () {
            if (navbar) resizeWindow()
        };

        let stickyLeft = navbar?.offsetLeft;
        let stickyMode = divModal.clientWidth > 768;
        let navbarItems = navbar?.getElementsByTagName('a');
        let scrollItems = new Array(navbarItems?.length);
        let sticky = navbar.offsetTop;
        if (navbarItems) {
            for (let i = 0; i < navbarItems.length; i++) {
                let item = navbarItems[i];
                scrollItems.push(item.attributes['href'].value);
            }
        }
        let lastId = "";

        function resizeWindow() {
            sticky = navbar.offsetTop;

            if (navbar && !navbar.classList.contains("sticky")) stickyLeft = navbar?.offsetLeft;
            stickyMode = divModal.clientWidth > 768;
            if (!stickyMode && navbar) {
                navbar.classList.remove("sticky");
                navbar.style.removeProperty('left');
            }
        }
    }
}

manageAsciidoc()
