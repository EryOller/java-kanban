package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.SubTask;
import model.Task;
import server.KVClient;
import service.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer extends InMemoryTaskManager{
    private final static String ID = "id=";
    TaskManager manager;
    HistoryManager history;
    HttpServer server;
    private static final int PORT = 8081;
    private Gson gson;
    private  KVClient kvClient = new KVClient(8078);

    public HttpTaskServer() throws IOException {
        this.manager = Managers.getDefault();
        this.history = Managers.getDefaultHistory();
        this.gson = Managers.getGson();
        ((HttpTaskManager) manager).load();

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::task);
        server.createContext("/tasks/subtask", this::subtask);
        server.createContext("/tasks/epic", this::epic);
        server.createContext("/tasks/history", this::history);
        server.createContext("/tasks/subtask/epic", this::epicSubTasks);
        server.createContext("/tasks", this::tasks);
    }

    private void tasks(HttpExchange httpExchange) {
        try {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange,  gson.toJson(getPrioritizedTasks()));
            } else {
                System.out.println("Ждем GET, а получили - " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void epicSubTasks(HttpExchange httpExchange) {
        try {
            String[] pathParts = httpExchange.getRequestURI().toString().split("/");
            if ("GET".equals(httpExchange.getRequestMethod())) {
                if (pathParts[pathParts.length - 1].indexOf(ID) != -1) {
                    int id = Integer.parseInt(pathParts[pathParts.length - 1].substring(ID.length() + 1));
                    sendText(httpExchange,  gson.toJson(getEpicById(id).getSubTasks()));
                }
            } else {
                System.out.println("Ждем GET, а получили - " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void history(HttpExchange httpExchange) {
        try {
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange,  gson.toJson(getListHistory()));
            } else {
                System.out.println("Ждем GET, а получили - " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void task(HttpExchange httpExchange) {
        try {
            String[] pathParts = httpExchange.getRequestURI().toString().split("/");
            String method = httpExchange.getRequestMethod();
            System.out.println(method);
            switch (method) {
                case "GET" : {
                    if (pathParts[pathParts.length - 1].indexOf(ID) != -1) {
                        int id = Integer.parseInt(pathParts[pathParts.length - 1].substring(ID.length() + 1));
                        getTaskByIDHandler(httpExchange, id);
                    } else {
                        getTaskHandler(httpExchange);
                    }
                    break;
                }
                case "POST" : {
                    postTaskHandler(httpExchange);
                    break;
                }
                case "DELETE" : {
                    if (pathParts[pathParts.length - 1].indexOf(ID) != -1) {
                        int id = Integer.parseInt(pathParts[pathParts.length - 1].substring(ID.length() + 1));
                        deleteTaskByIDHandler(httpExchange, id);
                    } else {
                        deleteTaskHandler(httpExchange);
                    }
                    break;
                }
                default : {
                    System.out.println("Ждем GET, POST или DELETE, а получили - " + method);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void postTaskHandler(HttpExchange httpExchange) {
        try {
            Task task = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), UTF_8), Task.class);
            if (getTaskById(task.getId()) != null) {
                updateTask(task.getId(), task);
                kvClient.save("tasks", gson.toJson(tasks).toString());
                sendText(httpExchange,  "Задача обновлена");
            } else {
                createTask(task);
                kvClient.save("tasks", gson.toJson(tasks).toString());
                sendText(httpExchange,  "Задача создана");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void deleteTaskHandler(HttpExchange httpExchange) {
        try {
            deleteAllTasks();
            kvClient.save("tasks", gson.toJson(tasks).toString());
            kvClient.save("epics", gson.toJson(epics).toString());
            kvClient.save("subtasks", gson.toJson(subTasks).toString());
            sendText(httpExchange,  "Удалены все такски");
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void deleteTaskByIDHandler(HttpExchange httpExchange, int id) {
        try {
            deleteTaskById(id);
            kvClient.save("tasks", gson.toJson(tasks).toString());
            sendText(httpExchange,  "Удалена задача с id = " + id);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getTaskByIDHandler(HttpExchange httpExchange, int id) {
        try {
            sendText(httpExchange, gson.toJson(getTaskById(id)));
            saveTaskInHistory(getTaskById(id));
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void epic(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().toString().split("/");
        String method = httpExchange.getRequestMethod();
        System.out.println(method);
        switch (method) {
            case "GET" : {
                if (pathParts[pathParts.length - 1].indexOf(ID) != -1) {
                    int id = Integer.parseInt(pathParts[pathParts.length - 1].substring(ID.length() + 1));
                    getEpicByIDHandler(httpExchange, id);
                } else {
                    getEpicHandler(httpExchange);
                }
                break;
            }
            case "POST" : {
                postEpicHandler(httpExchange);
                break;
            }
            case "DELETE" : {
                if (pathParts[pathParts.length - 1].indexOf(ID) != -1) {
                    int id = Integer.parseInt(pathParts[pathParts.length - 1].substring(ID.length() + 1));
                    deleteEpicByIDHandler(httpExchange, id);
                }
                break;
            }
            default : {
                System.out.println("Ждем GET, POST или DELETE, а получили - " + method);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void postEpicHandler(HttpExchange httpExchange) {
        try {
            Epic epic = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), UTF_8), Epic.class);
            if (getEpicById(epic.getId()) != null) {
                updateEpic(epic.getId(), epic);
                kvClient.save("epics", gson.toJson(epics).toString());
                sendText(httpExchange,  "Задача обновлена");
            } else {
                createEpic(epic);
                kvClient.save("epics", gson.toJson(epics).toString());
                sendText(httpExchange,  "Задача создана");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void deleteEpicByIDHandler(HttpExchange httpExchange, int id) {
        try {
            deleteEpicById(id);
            kvClient.save("epics", gson.toJson(epics).toString());
            sendText(httpExchange,  "Удален Epic с id = " + id);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getEpicByIDHandler(HttpExchange httpExchange, int id) {
        try {
            sendText(httpExchange, gson.toJson(getEpicById(id)));
            saveTaskInHistory(getEpicById(id));
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void subtask(HttpExchange httpExchange) throws IOException {
        String[] pathParts = httpExchange.getRequestURI().toString().split("/");
        String method = httpExchange.getRequestMethod();
        System.out.println(method);
        switch (method) {
            case "GET" : {
                if (pathParts[pathParts.length - 1].indexOf(ID) != -1) {
                    int id = Integer.parseInt(pathParts[pathParts.length - 1].substring(ID.length() + 1));
                    getSubTaskByIDHandler(httpExchange, id);
                } else {
                    getAllSubTaskHandler(httpExchange);
                }
                break;
            }
            case "POST" : {
                postSubTaskHandler(httpExchange);
                break;
            }
            case "DELETE" : {
                if (pathParts[pathParts.length - 1].indexOf(ID) != -1) {
                    int id = Integer.parseInt(pathParts[pathParts.length - 1].substring(ID.length() + 1));
                    deleteSubTaskByIDHandler(httpExchange, id);
                }
                break;
            }
            default : {
                System.out.println("Ждем GET, POST или DELETE, а получили - " + method);
                httpExchange.sendResponseHeaders(405, 0);
            }
        }
    }

    private void postSubTaskHandler(HttpExchange httpExchange) {
        try {
            SubTask subTask = gson.fromJson(new String(httpExchange.getRequestBody().readAllBytes(), UTF_8), SubTask.class);
            if (getSubTaskById(subTask.getId()) != null) {
                updateSubTask(subTask.getId(), subTask);
                kvClient.save("subtasks", gson.toJson(subTasks).toString());
                kvClient.save("epics", gson.toJson(epics).toString());
                sendText(httpExchange,  "Задача обновлена");
            } else {
                createSubTask(subTask, epics.get(subTask.getEpic()));
                kvClient.save("subtasks", gson.toJson(subTasks).toString());
                kvClient.save("epics", gson.toJson(epics).toString());
                sendText(httpExchange,  "Задача создана");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void deleteSubTaskByIDHandler(HttpExchange httpExchange, int id) {
        try {
            deleteSubTaskById(id);
            kvClient.save("subtasks", gson.toJson(subTasks).toString());
            kvClient.save("epics", gson.toJson(epics).toString());
            sendText(httpExchange,  "Удален SubTask с id = " + id);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getAllSubTaskHandler(HttpExchange httpExchange) {
        try {
            sendText(httpExchange, gson.toJson(getListAllSubTask()));
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getSubTaskByIDHandler(HttpExchange httpExchange, int id) {
        try {
            sendText(httpExchange, gson.toJson(getSubTaskById(id)));
            saveTaskInHistory(getSubTaskById(id));
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getTaskHandler(HttpExchange httpExchange) {
        try {
            sendText(httpExchange, gson.toJson(getListAllTask()));
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getEpicHandler(HttpExchange httpExchange) {
        try {
            sendText(httpExchange, gson.toJson(getListAllEpic()));
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порут " + PORT);
    }

    public static void main(String[] args) {
        final Gson gson = Managers.getGson();
        HashMap<Integer, Task> map1 = new HashMap<>();
        HashMap<Integer, Task> map2 = new HashMap<>();
        HashMap<Integer, Task> map3 = new HashMap<>();
        TaskManager taskManager =  new InMemoryTaskManager();
        int task1 = taskManager.createTask(new Task("Купить еды", "молоко и хлеб",
                LocalDateTime.of(2023,1,1,12,10), 100)).getId();

        int task2 = taskManager.createTask(new Task("Купить одежду", " майка и джинсы",
                LocalDateTime.of(2023,1,2,17,10), 50)).getId();


        int epic1 = taskManager.createEpic(new Epic("Работа", "Провести встречи")).getId();

        int subTask1 = taskManager.createSubTask(new SubTask("Новый сервис", "Встреча с Алексеем",
                LocalDateTime.of(2023,1,3,12,10), 100), taskManager.getEpicById(epic1)).getId();
        int subTask2 = taskManager.createSubTask(new SubTask("Тестирование", "Встреча с Иваном",
                LocalDateTime.of(2023,1,3,12,10), 100), taskManager.getEpicById(epic1)).getId();


      map1.put(task1, taskManager.getTaskById(task1));
      map1.put(task2, taskManager.getTaskById(task2));
      map2.put(epic1, taskManager.getEpicById(epic1));
      map3.put(subTask1, taskManager.getSubTaskById(subTask1));
      map3.put(subTask2, taskManager.getSubTaskById(subTask2));

        System.out.println(gson.toJson(map1));
        System.out.println(gson.toJson(map2));
        System.out.println(gson.toJson(map3));

    }
}
