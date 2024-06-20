async function updateTheme() {
    const colorMode = window.matchMedia("(prefers-color-scheme: dark)").matches ?
        "dark" :
        "light";
    if (document.querySelector("html").getAttribute("data-bs-theme-auto") === "auto") {
        //window.querySelector("html").setAttribute("data-bs-theme", colorMode);
        const response = await fetch("/theme/autoTheme?themeModeAuto=" + colorMode);
        console.log(response.text());

    }

    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', updateTheme)
}

// Set theme on load
updateTheme()

// Update theme when the preferred scheme changes
