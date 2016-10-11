package neopost2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

  private static Shell shell;
  private static Table table;

  public static void main(String[] args) throws IOException {
    query(true);

    Display display = new Display();
    shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
    shell.setSize(1280, 720);
    shell.setLayout(new GridLayout());
    Composite headerPane = new Composite(shell, SWT.NONE);
    GridData gd = new GridData();
    gd.heightHint = 80;
    gd.horizontalAlignment = SWT.FILL;
    headerPane.setLayoutData(gd);
    GridLayout gl = new GridLayout();
    gl.numColumns = 2;
    headerPane.setLayout(gl);

    Group filterPane = new Group(headerPane, SWT.NONE);
    filterPane.setText(" 필터 ");
    gd = new GridData();
    gd.verticalAlignment = SWT.FILL;
    gd.grabExcessHorizontalSpace = true;
    gd.grabExcessVerticalSpace = true;
    gd.horizontalAlignment = SWT.FILL;
    filterPane.setLayoutData(gd);

    Button qBtn = new Button(headerPane, SWT.PUSH);
    qBtn.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        table.clearAll();
        table.setRedraw(true);
        query(false);
        table.setItemCount(ApartmentRegistry.getInstance().getApartments().size());
        shell.setText("NeoPost2 [" + table.getItemCount() + "]");
      }
    });
    qBtn.setText("조회");
    gd = new GridData();
    gd.verticalAlignment = SWT.FILL;
    gd.grabExcessVerticalSpace = true;
    gd.horizontalAlignment = SWT.END;
    gd.widthHint = 60;
    qBtn.setLayoutData(gd);

    table = new Table(shell, SWT.VIRTUAL | SWT.FULL_SELECTION);
    table.setLayoutData(new GridData(GridData.FILL_BOTH));
    table.setHeaderVisible(true);

    TableColumn column = new TableColumn(table, SWT.LEFT);
    column.setText("시/도");
    column.setWidth(80);
    column = new TableColumn(table, SWT.LEFT);
    column.setText("구/군");
    column.setWidth(120);
    column = new TableColumn(table, SWT.LEFT);
    column.setText("동");
    column.setWidth(75);
    column = new TableColumn(table, SWT.LEFT);
    column.setText("단지");
    column.setWidth(180);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("전용면적");
    column.setWidth(60);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("평균가격");
    column.setWidth(80);
    column = new TableColumn(table, SWT.RIGHT);
    column.setText("거래건수");
    column.setWidth(60);

    table.addListener(SWT.SetData, event -> {
      TableItem item = (TableItem)event.item;
      int index = table.indexOf(item);
      Apartment apartment = ApartmentRegistry.getInstance().getApartments().get(index);
      Danji danji = apartment.getDanji();
      Dong dong = danji.getDong();
      Gugun gugun = dong.getGugun();
      Sido sido = gugun.getSido();
      item.setText(0, sido.getName());
      item.setText(1, gugun.getName());
      item.setText(2, dong.getName());
      item.setText(3, danji.getName());
      item.setText(4, apartment.getPyong() + "평");
      item.setText(5, apartment.getAveragePrice() / 10000.f + "억");
      item.setText(6, apartment.getDealCount() + "");
    });

    table.setItemCount(ApartmentRegistry.getInstance().getApartments().size());
    shell.setText("NeoPost2 [" + table.getItemCount() + "]건");

    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }

  private static void query(boolean useStored) {
    ApartmentRegistry.getInstance().clear();

    Country country = new Country(useStored);
    country.setTargetFilters(new String[]{"서울특별시", "경기도"});

    long startTime = System.nanoTime();

    ExecutorService es = Executors.newFixedThreadPool(64);
    CompletionService<String> ecs = new ExecutorCompletionService<>(es);
    AtomicInteger count = new AtomicInteger(1);

    ecs.submit(() -> {
      country.populate();
      for (Sido sido : country.getSidos()) {
        count.incrementAndGet();
        ecs.submit(() -> {
          sido.populate();
          for (Gugun gg : sido.getGuguns()) {
            count.incrementAndGet();
            ecs.submit(() -> {
              gg.populate();
              for (Dong dg : gg.getDongs()) {
                count.incrementAndGet();
                ecs.submit(() -> {
                  dg.populate();
                  for (Danji dj : dg.getDanjis()) {
                    count.incrementAndGet();
                    ecs.submit(() -> {
                      dj.populate();
                      return dj.getName();
                    });
                  }
                  return dg.getName();
                });
              }
              return gg.getName();
            });
          }
          return sido.getName();
        });
      }
      return "Country";
    });
    int i = 0;
    while (count.decrementAndGet() >= 0) {
      try {
        Future<String> f = ecs.take();
        long difference = System.nanoTime() - startTime;
        System.out.println((++i) + ".[" + difference / 1000000 + "] " + f.get() + " parsed.");
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    es.shutdown();
    ApartmentRegistry.getInstance().sortApartments();
  }

//    File input = new File(inputFile);
//    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
//    ArrayList<Apartment> apartments = new ArrayList<>();
//    Apartment apartment = null;
//    String line;
//    while ((line = reader.readLine()) != null) {
//      line = line.trim();
//      if (line.length() == 0) {
//        continue;
//      }
//      if ("{".equals(line)) {
//        apartment = new Apartment();
//        apartments.add(apartment);
//        continue;
//      }
//      if ("}".equals(line)) {
//        apartment = null;
//        continue;
//      }
//      if (apartment == null) {
//        continue;
//      }
//      INode node = directory.ROOT;
//      if (!apartment.hasDanjiNode()) {
//        for (String addr : line.split(",")) {
//          node = node.findNode(addr);
//          if (node == null) {
//            break;
//          }
//        }
//        DanjiNode danji = (DanjiNode)node;
//        if (danji == null) {
//          continue;
//        }
//        apartment.setName(line);
//        apartment.setDanjiNode(danji);
//      }
//      DanjiNode danji = apartment.getDanjiNode();
//      if (!danji.hasFocusedArea()) {
//        List<String> areas = new ArrayList<>();
//        for (String area : line.split("/")) {
//          areas.add(area);
//        }
//        danji.setFocusAreas(areas);
//      }
//    }
//    reader.close();
//
//    File output = new File(input.getParentFile(), input.getName() + ".out");
//    PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(output)));
//    for (Apartment ap : apartments) {
//      writer.println("{");
//      writer.print("  ");
//      writer.println(ap.getName());
//      writer.print("  ");
//      List<String> as = ap.getDanjiNode().getAreas();
//      if (as.size() > 0) {
//        for (int i = 0; i < as.size(); ++i) {
//          writer.print(as.get(i));
//          if (i != as.size() - 1) {
//            writer.print(" / ");
//          }
//        }
//        writer.println();
//      }
//      writer.println("}");
//    }
//    writer.close();
}
