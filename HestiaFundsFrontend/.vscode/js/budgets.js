document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = "http://localhost:8080/api";

    const budgetsTableBody = document.getElementById("budget-table-body");
    const saveBudgetsButton = document.getElementById("save-budgets");
    const viewEditCategoriesButton = document.getElementById("view-edit-categories");
    const backToTransactionsButton = document.getElementById("back-to-transactions");

    let categories = [];
    let budgets = [];

    //Fetch categories and budgets
    async function fetchCategoriesAndBudgets(){
        try{
            const [categoriesResponse, budgetsResponse] = await Promise.all([
                fetch(`${API_BASE_URL}/categories`),
                fetch(`${API_BASE_URL}/budgets`)
            ]);

            categories = await categoriesResponse.json();
            budgets = await budgetsResponse.json();

            populateBudgetsTable();
            showNotification("Categories and Budgets loaded successfully!", "success");
        }
        catch(error){
            console.error("Error fetching categories or budgets:", error);
            showNotification("Error fetching categories and/or budgets", "error");
        }
    }

    //Populate budgets table
    function populateBudgetsTable(){
        budgetsTableBody.innerHTML = "";

        categories.forEach(category => {
            const existingBudget = budgets.find(budget => budget.category.id === category.id);
            
            const row = document.createElement("tr");

            //Category name
            const categoryCell = document.createElement("td");
            categoryCell.textContent = category.name;
            row.appendChild(categoryCell);

            //Budget input
            const budgetCell = document.createElement("td");
            const budgetInput = document.createElement("input");
            budgetInput.type = "number";
            budgetInput.value = existingBudget ? existingBudget.amount : "";
            budgetInput.dataset.categoryId = category.id;
            budgetCell.appendChild(budgetInput);
            row.appendChild(budgetCell);

            //Total spent
            const spentCell = document.createElement("td");
            spentCell.textContent = existingBudget ? "0.00" : "0.00"; //Replace Me
            row.appendChild(spentCell);

            budgetsTableBody.appendChild(row);
        });
    }

    //Save budgets
    async function saveBudgets(){
        const updatedBudgets = Array.from(document.querySelectorAll("input[type='number']"))
            .filter(input => input.value)
            .map(input => ({
                category: { id: parseInt(input.dataset.categoryId) },
                amount: parseFloat(input.value),
                periodStart: "2025-01-01", //Replace Me
                periodEnd: "2025-01-31" //Replace Me
            }));

            try{
                const response = await fetch(`${API_BASE_URL}/budgets`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(updatedBudgets)
                });

                if(response.ok){
                    showNotification("Budgets saved successfully!", "success");
                }
                else{
                    console.error("Failed to save budget");
                    showNotification("Failed to save budget.", "error");
                }
            }
            catch(error){
                console.error("Error saving budgets:", error);
                showNotification("Error saving budget.", "error");
            }
    }

    //Notification
    function showNotification(message, type = "success"){
        const notificationContainer = document.getElementById("notification-container");
    
        //Create the notification element
        const notification = document.createElement("div");
        notification.className = `notification ${type}`;
        notification.textContent = message;
    
        //Add a close button
        const closeButton = document.createElement("button");
        closeButton.innerHTML = "&times;"
        closeButton.onclick = () => notification.remove();
        notification.appendChild(closeButton);
    
        //Append it to the container
        notificationContainer.appendChild(notification);
    
        //Remove it after 4 seconds
        setTimeout(() => {
            notification.remove();
        }, 4000);
    }

    saveBudgetsButton.addEventListener("click", saveBudgets);

    viewEditCategoriesButton.addEventListener("click", () => {
        window.location.href = "../html/categories.html";
    });

    // backToTransactionsButton.addEventListener("click", () => {
    //     window.location.href = "../html/transactions.html";
    // });

    //Initial fetch
    fetchCategoriesAndBudgets();
});