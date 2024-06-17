//= require bootstrap.bundle
//= require_self

function updateTheme() {
    const colorMode = window.matchMedia("(prefers-color-scheme: dark)").matches ?
        "dark" :
        "light";
    if (document.querySelector("html").getAttribute("data-bs-theme-auto") == "auto")
        document.querySelector("html").setAttribute("data-bs-theme", colorMode);

    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', updateTheme)
}

// Set theme on load
updateTheme()

// Update theme when the preferred scheme changes
