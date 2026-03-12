import java.time.LocalDate;
import java.util.*;

// ============================================================
// SyncMyDay 2.0 - Task Manager
// Demonstrates: LinkedList, Stack, Queue, Hashing,
//               Searching, and Sorting algorithms
// ============================================================

// ─────────────────────────────────────────────────────────────
// TASK MODEL
// ─────────────────────────────────────────────────────────────
class Task {
    int id;
    String title;
    int priority;   // 1 = highest, 5 = lowest
    String category;
    LocalDate date;
    boolean isCompleted;

    public Task(int id, String title, int priority, String category, LocalDate date, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.date = date;
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, title='%s', priority=%d, category='%s', date=%s, completed=%s}",
                id, title, priority, category, date, isCompleted);
    }
}

// ─────────────────────────────────────────────────────────────
// RUBRIC #4 — CUSTOM DOUBLY LINKED LIST
// Covers: Creation, Insertion, Deletion, Traversal, Search, Reverse
// ─────────────────────────────────────────────────────────────
class DoublyLinkedList {
    // Node with prev and next pointers
    static class Node {
        Task data;
        Node prev, next;
        Node(Task data) { this.data = data; }
    }

    Node head, tail;
    int size;

    /** Insert at the end — O(1) */
    public void insert(Task t) {
        Node newNode = new Node(t);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    /** Delete by task ID — O(n) */
    public boolean delete(int taskId) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.id == taskId) {
                // Unlink node
                if (cur.prev != null) cur.prev.next = cur.next;
                else head = cur.next;           // was head
                if (cur.next != null) cur.next.prev = cur.prev;
                else tail = cur.prev;           // was tail
                size--;
                System.out.println("Deleted from DLL: " + cur.data);
                return true;
            }
            cur = cur.next;
        }
        System.out.println("Task ID " + taskId + " not found in DLL.");
        return false;
    }

    /** Forward traversal */
    public void traverseForward() {
        System.out.println("\n[DLL] Forward traversal:");
        Node cur = head;
        int idx = 1;
        while (cur != null) {
            System.out.printf("  [%d] %s\n", idx++, cur.data);
            cur = cur.next;
        }
    }

    /** Reverse traversal (using prev pointers) */
    public void traverseReverse() {
        System.out.println("\n[DLL] Reverse traversal:");
        Node cur = tail;
        int idx = size;
        while (cur != null) {
            System.out.printf("  [%d] %s\n", idx--, cur.data);
            cur = cur.prev;
        }
    }

    /** Search by title — O(n) Linear Search on DLL */
    public Task searchByTitle(String query) {
        Node cur = head;
        while (cur != null) {
            if (cur.data.title.equalsIgnoreCase(query)) return cur.data;
            cur = cur.next;
        }
        return null;
    }

    /** Convert DLL to array for sorting/searching */
    public Task[] toArray() {
        Task[] arr = new Task[size];
        Node cur = head;
        int i = 0;
        while (cur != null) { arr[i++] = cur.data; cur = cur.next; }
        return arr;
    }
}

// ─────────────────────────────────────────────────────────────
// RUBRIC #6 — CIRCULAR QUEUE
// Fixed-size circular buffer for reminders
// ─────────────────────────────────────────────────────────────
class CircularQueue {
    private Task[] buffer;
    private int front, rear, count, capacity;

    public CircularQueue(int capacity) {
        this.capacity = capacity;
        buffer = new Task[capacity];
        front = rear = count = 0;
    }

    /** Enqueue — O(1) */
    public boolean enqueue(Task t) {
        if (count == capacity) {
            System.out.println("[CircularQueue] Queue full! Cannot add: " + t.title);
            return false;
        }
        buffer[rear] = t;
        rear = (rear + 1) % capacity;   // wrap around
        count++;
        return true;
    }

    /** Dequeue — O(1) */
    public Task dequeue() {
        if (count == 0) return null;
        Task t = buffer[front];
        front = (front + 1) % capacity; // wrap around
        count--;
        return t;
    }

    public boolean isEmpty() { return count == 0; }
    public int size()        { return count; }

    public void display() {
        System.out.println("\n[CircularQueue] " + count + "/" + capacity + " slots used:");
        for (int i = 0; i < count; i++) {
            System.out.println("  -> " + buffer[(front + i) % capacity]);
        }
    }
}

// ─────────────────────────────────────────────────────────────
// RUBRIC #7 — CUSTOM HASH TABLE (Separate Chaining)
// Hash function + insert / search / delete
// ─────────────────────────────────────────────────────────────
class HashTable {
    private static final int TABLE_SIZE = 13; // prime for better distribution
    private LinkedList<Task>[] table;

    @SuppressWarnings("unchecked")
    public HashTable() {
        table = new LinkedList[TABLE_SIZE];
        for (int i = 0; i < TABLE_SIZE; i++) table[i] = new LinkedList<>();
    }

    /** Custom hash function — polynomial rolling hash on title */
    private int hash(String key) {
        int h = 0;
        for (char c : key.toCharArray()) {
            h = (h * 31 + c) % TABLE_SIZE;
        }
        return Math.abs(h);
    }

    /** Insert task — O(1) amortised */
    public void insert(Task t) {
        int idx = hash(t.title);
        // Avoid duplicate IDs
        table[idx].removeIf(existing -> existing.id == t.id);
        table[idx].add(t);
        System.out.printf("[HashTable] Inserted '%s' at bucket %d\n", t.title, idx);
    }

    /** Search by title — O(1) amortised */
    public Task search(String title) {
        int idx = hash(title);
        for (Task t : table[idx]) {
            if (t.title.equalsIgnoreCase(title)) return t;
        }
        return null;
    }

    /** Delete by title — O(1) amortised */
    public boolean delete(String title) {
        int idx = hash(title);
        return table[idx].removeIf(t -> t.title.equalsIgnoreCase(title));
    }

    /** Display all buckets */
    public void display() {
        System.out.println("\n[HashTable] Contents:");
        for (int i = 0; i < TABLE_SIZE; i++) {
            if (!table[i].isEmpty()) {
                System.out.printf("  Bucket[%2d]: ", i);
                for (Task t : table[i]) System.out.print(t.title + " -> ");
                System.out.println("null");
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// RUBRIC #3 — SORTING ALGORITHMS
// Basic: Bubble, Insertion | Advanced: Merge, Quick
// ─────────────────────────────────────────────────────────────
class SortingAlgorithms {

    // --- Basic: Bubble Sort by priority — O(n²) ---
    public static void bubbleSort(Task[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].priority > arr[j + 1].priority) {
                    Task temp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = temp;
                }
            }
        }
    }

    // --- Basic: Insertion Sort by title alphabetically — O(n²) ---
    public static void insertionSort(Task[] arr) {
        for (int i = 1; i < arr.length; i++) {
            Task key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j].title.compareToIgnoreCase(key.title) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    // --- Advanced: Merge Sort by date — O(n log n) ---
    public static void mergeSort(Task[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private static void merge(Task[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1, n2 = right - mid;
        Task[] L = new Task[n1], R = new Task[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i].date.compareTo(R[j].date) <= 0) arr[k++] = L[i++];
            else arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // --- Advanced: Quick Sort by priority — O(n log n) avg ---
    public static void quickSort(Task[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(Task[] arr, int low, int high) {
        int pivot = arr[high].priority;
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j].priority <= pivot) {
                i++;
                Task temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        Task temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
        return i + 1;
    }

    public static void print(Task[] arr) {
        for (int i = 0; i < arr.length; i++)
            System.out.printf("  [%d] %s\n", i + 1, arr[i]);
    }
}

// ─────────────────────────────────────────────────────────────
// RUBRIC #2 — SEARCHING ALGORITHMS
// Linear Search + Binary Search
// ─────────────────────────────────────────────────────────────
class SearchAlgorithms {

    /**
     * Linear Search — O(n)
     * Searches unsorted list by title substring
     */
    public static List<Task> linearSearch(Task[] arr, String query) {
        List<Task> results = new ArrayList<>();
        for (Task t : arr) {
            if (t.title.toLowerCase().contains(query.toLowerCase())) results.add(t);
        }
        return results;
    }

    /**
     * Binary Search — O(log n)
     * Array MUST be sorted by title (use insertionSort first)
     */
    public static Task binarySearch(Task[] sortedArr, String query) {
        int low = 0, high = sortedArr.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = sortedArr[mid].title.compareToIgnoreCase(query);
            if (cmp == 0)      return sortedArr[mid];
            else if (cmp < 0)  low = mid + 1;
            else               high = mid - 1;
        }
        return null;
    }
}

// ─────────────────────────────────────────────────────────────
// RUBRIC #5 — STACK APPLICATION: Parentheses Checker
// ─────────────────────────────────────────────────────────────
class ParenthesesChecker {
    /**
     * Uses a Stack to verify balanced brackets — O(n)
     * Valid pairs: () [] {}
     */
    public static boolean isBalanced(String expression) {
        Stack<Character> stack = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((c == ')' && top != '(') ||
                    (c == ']' && top != '[') ||
                    (c == '}' && top != '{')) return false;
            }
        }
        return stack.isEmpty();
    }
}

// ─────────────────────────────────────────────────────────────
// MAIN APPLICATION
// ─────────────────────────────────────────────────────────────
public class SyncMyDay {

    // Core data structures
    private static DoublyLinkedList dll       = new DoublyLinkedList();
    private static Stack<Task>       undoStack = new Stack<>();
    private static CircularQueue     circQueue = new CircularQueue(10);
    private static PriorityQueue<Task> urgentQ = new PriorityQueue<>((t1, t2) ->
            Integer.compare(t1.priority, t2.priority));
    private static HashTable         hashTable = new HashTable();
    private static int nextId = 1;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║      SyncMyDay 2.0           ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║ -- Task Operations --        ║");
            System.out.println("║  1. Add Task                 ║");
            System.out.println("║  2. Delete Task (DLL)        ║");
            System.out.println("║  3. Undo Last Add (Stack)    ║");
            System.out.println("║ -- Sorting --                ║");
            System.out.println("║  4. Bubble Sort (Priority)   ║");
            System.out.println("║  5. Insertion Sort (Title)   ║");
            System.out.println("║  6. Merge Sort (Date)        ║");
            System.out.println("║  7. Quick Sort (Priority)    ║");
            System.out.println("║ -- Searching --              ║");
            System.out.println("║  8. Linear Search            ║");
            System.out.println("║  9. Binary Search (by title) ║");
            System.out.println("║ -- Data Structures --        ║");
            System.out.println("║ 10. View DLL (Forward)       ║");
            System.out.println("║ 11. View DLL (Reverse)       ║");
            System.out.println("║ 12. Circular Queue View      ║");
            System.out.println("║ 13. Process Next Reminder    ║");
            System.out.println("║ 14. Top Urgent Task          ║");
            System.out.println("║ 15. Hash Table View          ║");
            System.out.println("║ 16. Hash Table Search        ║");
            System.out.println("║ 17. Parentheses Checker      ║");
            System.out.println("║ 18. Exit                     ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Choose: ");

            int choice;
            try { choice = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a number."); continue; }

            switch (choice) {
                case 1  -> addTask(sc);
                case 2  -> deleteTask(sc);
                case 3  -> undoLastAdd();
                case 4  -> demoBubbleSort();
                case 5  -> demoInsertionSort();
                case 6  -> demoMergeSort();
                case 7  -> demoQuickSort();
                case 8  -> linearSearchDemo(sc);
                case 9  -> binarySearchDemo(sc);
                case 10 -> dll.traverseForward();
                case 11 -> dll.traverseReverse();
                case 12 -> circQueue.display();
                case 13 -> processNextReminder();
                case 14 -> topUrgentTask();
                case 15 -> hashTable.display();
                case 16 -> hashSearchDemo(sc);
                case 17 -> parenthesesDemo(sc);
                case 18 -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ── ADD TASK ───────────────────────────────────────────────
    public static void addTask(Scanner sc) {
        System.out.print("Enter title: ");
        String title = sc.nextLine();
        System.out.print("Enter priority (1=highest, 5=lowest): ");
        int priority;
        try { priority = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid priority."); return; }
        System.out.print("Enter category: ");
        String category = sc.nextLine();

        Task task = new Task(nextId++, title, priority, category, LocalDate.now(), false);

        // Add to all data structures
        dll.insert(task);
        hashTable.insert(task);
        if (priority <= 2) circQueue.enqueue(task);  // high-priority → reminder
        if (priority <= 3) urgentQ.offer(task);
        undoStack.push(task);

        System.out.println("Task added: " + task);
    }

    // ── DELETE TASK ────────────────────────────────────────────
    public static void deleteTask(Scanner sc) {
        System.out.print("Enter task ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            dll.delete(id);
        } catch (NumberFormatException e) { System.out.println("Invalid ID."); }
    }

    // ── UNDO (STACK) ───────────────────────────────────────────
    public static void undoLastAdd() {
        if (undoStack.isEmpty()) { System.out.println("Nothing to undo."); return; }
        Task last = undoStack.pop();
        dll.delete(last.id);
        urgentQ.remove(last);
        hashTable.delete(last.title);
        System.out.println("Undone: " + last);
    }

    // ── SORTING DEMOS ──────────────────────────────────────────
    public static void demoBubbleSort() {
        Task[] arr = dll.toArray();
        if (arr.length == 0) { System.out.println("No tasks."); return; }
        // Time complexity: O(n²) — simple but inefficient for large n
        SortingAlgorithms.bubbleSort(arr);
        System.out.println("\n[Bubble Sort] By priority (1=highest) — O(n²):");
        SortingAlgorithms.print(arr);
    }

    public static void demoInsertionSort() {
        Task[] arr = dll.toArray();
        if (arr.length == 0) { System.out.println("No tasks."); return; }
        // Time complexity: O(n²) worst, O(n) best (nearly sorted)
        SortingAlgorithms.insertionSort(arr);
        System.out.println("\n[Insertion Sort] By title A→Z — O(n²):");
        SortingAlgorithms.print(arr);
    }

    public static void demoMergeSort() {
        Task[] arr = dll.toArray();
        if (arr.length == 0) { System.out.println("No tasks."); return; }
        // Time complexity: O(n log n) — divide and conquer
        SortingAlgorithms.mergeSort(arr, 0, arr.length - 1);
        System.out.println("\n[Merge Sort] By date — O(n log n):");
        SortingAlgorithms.print(arr);
    }

    public static void demoQuickSort() {
        Task[] arr = dll.toArray();
        if (arr.length == 0) { System.out.println("No tasks."); return; }
        // Time complexity: O(n log n) avg, O(n²) worst
        SortingAlgorithms.quickSort(arr, 0, arr.length - 1);
        System.out.println("\n[Quick Sort] By priority — O(n log n) avg:");
        SortingAlgorithms.print(arr);
    }

    // ── SEARCHING DEMOS ────────────────────────────────────────
    public static void linearSearchDemo(Scanner sc) {
        System.out.print("Enter title keyword to search (Linear — O(n)): ");
        String query = sc.nextLine();
        Task[] arr = dll.toArray();
        // Linear Search: checks every element — O(n)
        List<Task> results = SearchAlgorithms.linearSearch(arr, query);
        if (results.isEmpty()) System.out.println("No results found.");
        else results.forEach(t -> System.out.println("  Found: " + t));
    }

    public static void binarySearchDemo(Scanner sc) {
        System.out.print("Enter EXACT title to search (Binary — O(log n)): ");
        String query = sc.nextLine();
        Task[] arr = dll.toArray();
        if (arr.length == 0) { System.out.println("No tasks."); return; }
        // Binary Search requires sorted array — sort first by title
        SortingAlgorithms.insertionSort(arr);
        Task result = SearchAlgorithms.binarySearch(arr, query);
        if (result == null) System.out.println("Task not found (Binary Search — O(log n)).");
        else System.out.println("Binary Search found: " + result);
    }

    // ── QUEUE & PRIORITY QUEUE ─────────────────────────────────
    public static void processNextReminder() {
        Task next = circQueue.dequeue();
        if (next == null) { System.out.println("No pending reminders."); return; }
        next.isCompleted = true;
        System.out.println("Reminder processed (marked complete): " + next);
    }

    public static void topUrgentTask() {
        if (urgentQ.isEmpty()) { System.out.println("No urgent tasks."); return; }
        System.out.println("Top urgent task: " + urgentQ.peek());
    }

    // ── HASH TABLE SEARCH ──────────────────────────────────────
    public static void hashSearchDemo(Scanner sc) {
        System.out.print("Enter exact title to hash-search: ");
        String title = sc.nextLine();
        Task result = hashTable.search(title);
        if (result == null) System.out.println("Not found in hash table.");
        else System.out.println("Hash Search found: " + result);
    }

    // ── STACK APPLICATION: PARENTHESES CHECKER ─────────────────
    public static void parenthesesDemo(Scanner sc) {
        System.out.print("Enter expression to check parentheses (e.g. {[()]}): ");
        String expr = sc.nextLine();
        boolean balanced = ParenthesesChecker.isBalanced(expr);
        System.out.println("Expression \"" + expr + "\" is " + (balanced ? "BALANCED ✓" : "NOT balanced ✗"));
    }
}
