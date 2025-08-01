let lang = "cs"; // Язык по умолчанию
let t = {};      // Объект перевода


async function loadLang(langCode) {
  const res = await fetch(`./lang/${langCode}.json`);
  t = await res.json();
  lang = langCode;
  applyTranslations();
}

function applyTranslations() {
  document.getElementById("searchInput").placeholder = t.placeholder;
  document.getElementById("startBtn").textContent = t.start;
  document.getElementById("saveBtn").textContent = t.save;
  document.getElementById("footer1").textContent = t.footer1;
  document.getElementById("footer2").textContent = t.footer2;
}

// Переключение языка
document.getElementById("langSwitcher").addEventListener("change", (e) => {
  loadLang(e.target.value);
});

// Применяем тему до загрузки DOM
if (!localStorage.getItem("theme")) {
  localStorage.setItem("theme", "dark");
}
if (localStorage.getItem("theme") === "dark") {
  document.documentElement.classList.add("dark");
}

// Основной обработчик поиска
document.addEventListener("DOMContentLoaded", () => {
  const input = document.getElementById("searchInput");
  const startBtn = document.getElementById("startBtn");
  const saveBtn = document.getElementById("saveBtn");
  const statusText = document.getElementById("statusText");

  saveBtn.style.display = "none";
  statusText.textContent = "";

  startBtn.addEventListener("click", async () => {
    const query = input.value.trim();
    if (!query) return;

    saveBtn.style.display = "none";
    statusText.textContent = t.loading;

    try {
      const response = await fetch(`https://iniziotest-production.up.railway.app/search?q=${encodeURIComponent(query)}`);
      if (!response.ok) throw new Error(`Ошибка: ${response.status}`);

      statusText.textContent = t.done;
      saveBtn.style.display = "inline-block";

      setTimeout(() => {
        statusText.textContent = "";
      }, 3000);

      saveBtn.onclick = () => {
        const link = document.createElement("a");
        link.href = `https://iniziotest-production.up.railway.app/download?q=${encodeURIComponent(query)}`;
        link.download = "results.json";
        link.click();
      };
    } catch (e) {
      console.error(e);
      statusText.textContent = t.error;
    }
  });

  // Загружаем язык при старте
  loadLang(lang);

  const themeToggle = document.getElementById("themeToggle");



// Установить положение переключателя при загрузке
  themeToggle.checked = document.documentElement.classList.contains("light");

// Слушатель переключения темы
  themeToggle.addEventListener("change", () => {
    document.documentElement.classList.toggle("dark");
    const newTheme = document.documentElement.classList.contains("dark") ? "light" : "dark";
    localStorage.setItem("theme", newTheme);
  });

});
