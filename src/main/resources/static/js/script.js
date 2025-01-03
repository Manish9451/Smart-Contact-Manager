

const toggleSidebar = () => {

    const sidebar = document.querySelector('.sidebar');
    const content = document.querySelector('.content');

    if (sidebar.style.display === "block") {
        sidebar.style.display = "none";
        content.style.marginLeft = "0%";
    } else {
        sidebar.style.display = "block";
        content.style.marginLeft = "20%";
    }
};




// searching
const search = () => {
    const query = document.getElementById("search-input").value.trim();

    if (query === "") {
        document.querySelector(".search-result").style.display = "none";
    } else {
        const url = `/search/${query}`; // Adjust the URL as per your backend route

        fetch(url)
            .then(response => response.json())
            .then(data => {
                const resultContainer = document.querySelector(".search-result");
                resultContainer.innerHTML = "<h5>Search Result</h5>"; // Clear previous results

                if (data && data.length > 0) {
                    data.forEach(contact => {
                        const div = document.createElement("div");
                        div.innerHTML = `
                            <p>
                                <a href="/user/${contact.cId}/contact" style="text-decoration: none; color: green;">
                                    ${contact.name}
                                </a>
                            </p>`;
                        resultContainer.appendChild(div);
                    });
                } else {
                    resultContainer.innerHTML += "<p>No results found.</p>";
                }

                resultContainer.style.display = "block";
            })
            .catch(error => {
                console.error("Error fetching search results:", error);
            });
    }
};
