async function updateTheme() {
    const colorMode = window.matchMedia("(prefers-color-scheme: dark)").matches ?
        "dark" :
        "light";
    console.log("colorMode: " + colorMode + " data-bs-theme " + document.querySelector("html").getAttribute("data-bs-theme"));
    if (document.querySelector("html").getAttribute("data-bs-theme-auto") === "auto" && document.querySelector("html").getAttribute("data-bs-theme") !== colorMode) {
        console.log("colorMode IF true");
        window.document.querySelector("html").setAttribute("data-bs-theme", colorMode);
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', updateTheme);
        await fetch("/theme/autoTheme?themeModeAuto=" + colorMode);
    }
}

// Set theme on load
updateTheme()

// Update theme when the preferred scheme changes
