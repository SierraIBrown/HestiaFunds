document.addEventListener("DOMContentLoaded", () => {
    const API_BASE_URL = "http://localhost:8080/api";

    const calendarGrid = document.getElementById("calendar-grid");
    const monthYearHeader = document.getElementById("month-year");
    const prevMonthButton = document.getElementById("prev-month");
    const nextMonthButton = document.getElementById("next-month");
    const transactionForm = document.getElementById("transaction-form");
    const categorySelect = document.getElementById("category");

    let currentDate = new Date();


    //Fetch transaction for the current month and display them on the calendar
    async function fetchTransactionsForMonth(year, month){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions?year=${year}&month=${month + 1}`);
            const transactions = await response.json();
            populateCalendar(year, month, transactions);
        }
        catch(error){
            console.error("Error fetching transactions:", error);
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

            const dayTransactions = transactions.filter(
                (transaction) => new Date(transaction.date).getDate() === day
            );

            dayTransactions.forEach((transaction) => {
                const transactionDiv = document.createElement("div");
                transactionDiv.className = "transaction";
                transactionDiv.textContent = `${transaction.amount.toFixed(2)} - ${transaction.category.name}`;
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

            categories.forEach((category) => {
                const option = document.createElement("option");
                option.value = category.id;
                option.textContent = category.name;
                categorySelect.appendChild(option);
            });
        }
        catch(error){
            console.error("Error fetching categories:", error);
        }
    }

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
                alert("Transaction updated successfully!");
                fetchTransactions();
            }
            else{
                alert("Failed to update transaction.");
            }
        }
        catch(error){
            console.error("Error updating transaction:", error);
        }
    }

    async function deleteTransaction(id){
        try{
            const response = await fetch(`${API_BASE_URL}/transactions/${id}`, {
                method: "DELETE",
            });

            if(response.ok){
                alert("Transaction deleted successfully!");
                fetchTransactions();
            }
            else{
                alert("Failed to delete transaction.");
            }
        }
        catch(error){
            console.error("Error deleting transaction:", error);
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
                alert("All fields are required for editing a transaction.");
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
                alert("Transaction added successfully!");
                fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
                transactionForm.reset();
            } else {
                alert("Failed to add transaction.");
            }
        } catch (error) {
            console.error("Error adding transaction:", error);
        }
    });

    //Initial fetches
    fetchCategories();
    fetchTransactionsForMonth(currentDate.getFullYear(), currentDate.getMonth());
});