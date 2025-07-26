package com.example.contractorsapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.contractorsapp.data.local.AppDatabase;
import com.example.contractorsapp.data.local.ContragentDao;
import com.example.contractorsapp.data.model.Contragent;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class ContragentDaoTest {

    // Това правило е задължително при тестване на LiveData.
    // То кара всички фонови задачи да се изпълняват на същата нишка, веднага.
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ContragentDao contragentDao;
    private AppDatabase db;

    // Този метод се изпълнява ПРЕДИ всеки тест
    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        // Създаваме временна база данни само в паметта, която се унищожава след теста.
        // Това гарантира, че тестовете са изолирани и не си пречат.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // Позволяваме заявки на главната нишка (САМО ЗА ТЕСТОВЕ!)
                .build();
        contragentDao = db.contragentDao();
    }

    // Този метод се изпълнява СЛЕД всеки тест
    @After
    public void closeDb() throws IOException {
        db.close();
    }

    // Това е нашият тест, който проверява запис и четене
    @Test
    public void insertAndGetContragent() throws Exception {
        // 1. ПОДГОТОВКА: Създаваме примерен контрагент
        Contragent contragent = new Contragent();
        contragent.setId(999);
        contragent.setName("Тестов Контрагент ООД");
        contragent.setPhone("0888123456");
        contragent.setAddress("гр. Тестов, ул. Кодърска 1");
        contragent.setBulstat("123456789");
        contragent.setEmail("test@example.com");

        // Създаваме списък, защото твоят метод insertAll очаква списък
        java.util.List<Contragent> contragentList = new java.util.ArrayList<>();
        contragentList.add(contragent);

        // 2. ДЕЙСТВИЕ: Записваме го в базата чрез insertAll
        contragentDao.insertAll(contragentList);

        // 3. ПРОВЕРКА: Изчитаме го директно с новия синхронен метод
        Contragent contragentFromDb = contragentDao.getContragentByIdSync(999);

        // Проверяваме дали резултатът е това, което очакваме
        assertNotNull("Записаният контрагент не беше намерен в базата.", contragentFromDb);
        assertEquals("Името на контрагента не съвпада.", contragent.getName(), contragentFromDb.getName());
        assertEquals("Телефонът на контрагента не съвпада.", contragent.getPhone(), contragentFromDb.getPhone());
    }
}