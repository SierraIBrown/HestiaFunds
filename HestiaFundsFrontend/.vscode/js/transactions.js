document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = "http://localhost:8080/api";

    const calendarGrid = document.getElementById("calendar-grid");
    const monthYearHeader = document.getElementById("month-year");
    const prevMonthButton = document.getElementById("prev-month");
    const nextMonthButton = document.getElementById("next-month");
    const transactionForm = document.getElementById("transaction-form");

    let currentDate = new Date();


    //Fetch transaction for the current month and display them on the calendar
    async function fetchTransactionsForMonth(year, month){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions?year=${year}&month=${month + 1}`);
            const transactions = await response.json();
            populateCalendar(year, month, transactions);
            showNotification("Transactions for the month loaded.", "info");
        }
        catch(error){
            console.error("Error fetching transactions:", error);
            showNotification("Error fetching transactions.", "error");
        }
    }

    //Populate the calendar grid
    function populateCalendar(year, month, transactions){
        calendarGrid.innerHTML = ''; //Clear the previous calendar

        const firstDay = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        //Add empty blocks for days before the first of the month
        for(let i = 0; i < firstDay; i++){
            const emptyBlock = document.createElement("div");
            emptyBlock.className = "calendar-day empty";
            calendarGrid.appendChild(emptyBlock);
        }

        //Add day blocks with transactions
        for(let day = 1; day <= daysInMonth; day++){
            const dayBlock = document.createElement("div");
            dayBlock.className = "calendar-day";
            dayBlock.innerHTML = `<div class="day-number">${day}</div>`;

            const dayTransactions = transactions.filter((transaction) => {
                const transactionDate = new Date(transaction.date);
                return(
                    transactionDate.getUTCFullYear() === year &&
                    transactionDate.getUTCMonth() === month &&
                    transactionDate.getUTCDate() === day
                );
            });

            dayTransactions.forEach((transaction) => {
                const transactionDiv = document.createElement("div");
                transactionDiv.className = "transaction";

                //Create the category tag
                const categoryTag = document.createElement("span");
                categoryTag.className = "category-tag";
                categoryTag.textContent = transaction.category.name;
                categoryTag.style.backgroundColor = transaction.category.color || "#ddd";

                //Create the transaction amount element
                const transactionAmount = document.createElement("span");
                transactionAmount.classname = "transaction-amount";
                transactionAmount.textContent = `$${transaction.amount.toFixed(2)}`;

                transactionDiv.appendChild(categoryTag);
                transactionDiv.appendChild(transactionAmount);
                dayBlock.appendChild(transactionDiv);
            });

            calendarGrid.appendChild(dayBlock);
        }

        //Update month and year header
        monthYearHeader.textContent = `${currentDate.toLocaleString("default", {
            month: "long",
        })} ${currentDate.getFullYear()}`;
    }
    

    //Handle month navigation
    prevMonthButton.addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
    });

    nextMonthButton.addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
    });

    //Fetch categories for the form
    async function fetchCategories(){
        try{
            const response = await fetch(`${API_BASE_URL}/categories`);
            const categories = await response.json();

            const categorySelect = document.getElementById("category");
            categorySelect.innerHTML = "";

            //Add a default option
            const defaultOption = document.createElement("option");
            defaultOption.value = "";
            defaultOption.disabled = true;
            defaultOption.selected = true;
            defaultOption.textContent = "Select a category";
            categorySelect.appendChild(defaultOption);

            //Populate the dropdown with categories
            categories.forEach((category) => {
                const option = document.createElement("option");
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });

            //Add the Edit/View Categories option
            const editViewOption = document.createElement("option");
            editViewOption.value = "editCategories";
            editViewOption.textContent = "Edit/View Categories...";
            categorySelect.appendChild(editViewOption);

            showNotification("Categories fetched successfully.", "success");
        }
        catch(error){
            console.error("Error fetching categories:", error);
            showNotification("Error fetching categories.", "error");
        }
    }

    //Handle category selection
    document.getElementById("category").addEventListener("change", (event) => {
        if(event.target.value === "editCategories"){
            window.location.href = "categories.html";
        }
    });

    async function editTransaction(transaction){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions/${transaction.id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(transaction),
            });

            if(response.ok){
                showNotification("Transaction updated successfully!", "success");
                fetchTransactionsForMonth();
            }
            else{
                showNotification("Failed to update transaction.", "error");
            }
        }
        catch(error){
            console.error("Error updating transaction:", error);
            showNotification("Error updating transaction", "error");
        }
    }

    async function deleteTransaction(id){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions/${id}`, {
                method: "DELETE",
            });

            if(response.ok){
                showNotification("Transaction deleted successfully!", "success");
                fetchTransactionsForMonth();
            }
            else{
                showNotification("Failed to delete transaction.", "error");
            }
        }
        catch(error){
            console.error("Error deleting transaction:", error);
            showNotification("Error deleting transaction.", "error");
        }
    }

    //Handle calendar transaction button clicks
    document.getElementById("calendar-grid").addEventListener("click", async (event) => {
        const target = event.target;

        //Edit button
        if(target.classList.contains("edit-btn")){
            const id = target.dataset.id;
            const transactionDiv = target.closest(".transaction");

            const amount = prompt(
                "Enter new amount:",
                transactionDiv.dataset.amount
            );
            const date = prompt(
                "Enter new date (YYYY-MM-DD):",
                transactionDiv.dataset.date
            );
            const description = prompt(
                "Enter new category ID:",
                transactionDiv.dataset.categoryId
            );

            if(amount && date && description && categoryId){
                const updatedTransaction = {
                    id: id,
                    amount: parseFloat(amount),
                    date: date,
                    description: description,
                    category: { id: parseInt(categoryId) },
                };

                await editTransaction(updatedTransaction);
            }
            else{
                showNotification("All fields are required for editing a transaction.", "warning");
            }
        }

        //Delete button
        if(target.classList.contains("delete-btn")){
            const id = target.dataset.id;

            if(confirm("Are you sure you want to delete this transaction?")){
                await deleteTransaction(id);
            }
        }
    });

    // Submit a new transaction
    transactionForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const transaction = {
            amount: parseFloat(document.getElementById("amount").value),
            date: document.getElementById("date").value,
            description: document.getElementById("description").value,
            category: { id: parseInt(document.getElementById("category").value) },
        };

        try {
            const response = await fetch(`${API_BASE_URL}/transactions`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(transaction),
            });

            if (response.ok) {
                showNotification("Transaction added successfully!", "success");
                fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
                transactionForm.reset();
            } else {
                showNotification("Failed to add transaction.", "error");
            }
        } catch (error) {
            console.error("Error adding transaction:", error);
            showNotification("Error adding transaction.", "error");
        }
    });

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

    //Initial fetches
    fetchCategories();
    fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
});