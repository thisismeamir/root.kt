// generate_test_fixtures.C
// Run with: root -l -b -q generate_test_fixtures.C
// Generates small but complete .root files covering different ROOT object types.

#include <TFile.h>
#include <TTree.h>
#include <TH1F.h>
#include <TH2F.h>
#include <TProfile.h>
#include <TNtuple.h>
#include <TDirectory.h>
#include <TString.h>
#include <TRandom3.h>
#include <TGraph.h>

void generate(const char* path, std::function<void(TFile*)> fill) {
    TFile f(path, "RECREATE");
    fill(&f);
    f.Write();
    f.Close();
    printf("Written: %s\n", path);
}

void generate_all(const char* outdir = ".") {
    TRandom3 rng(42);
    TString base(outdir);

    // 1. simple_th1.root — single TH1F, zlib compressed
    generate(base + "/simple_th1.root", [&](TFile* f) {
        TH1F h("h1", "Simple histogram;x;counts", 100, -5, 5);
        for (int i = 0; i < 1000; i++) h.Fill(rng.Gaus(0, 1));
        h.Write();
    });

    // 2. simple_th2.root — TH2F
    generate(base + "/simple_th2.root", [&](TFile* f) {
        TH2F h("h2", "2D histogram;x;y", 50, -5, 5, 50, -5, 5);
        for (int i = 0; i < 1000; i++) h.Fill(rng.Gaus(0,1), rng.Gaus(0,1));
        h.Write();
    });

    // 3. simple_ttree.root — TTree with basic branches (int, float, double)
    generate(base + "/simple_ttree.root", [&](TFile* f) {
        TTree t("events", "Simple event tree");
        int    run; float pt; double eta; bool flag;
        t.Branch("run",  &run,  "run/I");
        t.Branch("pt",   &pt,   "pt/F");
        t.Branch("eta",  &eta,  "eta/D");
        t.Branch("flag", &flag, "flag/O");
        for (int i = 0; i < 500; i++) {
            run = i; pt = rng.Gaus(30, 5); eta = rng.Uniform(-2.5, 2.5); flag = (i % 2 == 0);
            t.Fill();
        }
        t.Write();
    });

    // 4. array_branches.root — TTree with fixed and variable-length arrays
    generate(base + "/array_branches.root", [&](TFile* f) {
        TTree t("events", "Array branch tree");
        int n; float vals[10]; int hits[5];
        t.Branch("n",    &n,    "n/I");
        t.Branch("vals", vals,  "vals[10]/F");
        t.Branch("hits", hits,  "hits[5]/I");
        for (int i = 0; i < 200; i++) {
            n = i % 10;
            for (int j = 0; j < 10; j++) vals[j] = rng.Gaus(0,1);
            for (int j = 0; j < 5;  j++) hits[j] = rng.Integer(100);
            t.Fill();
        }
        t.Write();
    });

    // 5. subdirectory.root — TFile with nested TDirectories
    generate(base + "/subdirectory.root", [&](TFile* f) {
        TDirectory* d1 = f->mkdir("detector");
        TDirectory* d2 = f->mkdir("trigger");
        d1->cd();
        TH1F h1("hits", "Detector hits;n;counts", 50, 0, 50);
        for (int i = 0; i < 500; i++) h1.Fill(rng.Poisson(10));
        h1.Write();
        d2->cd();
        TH1F h2("rate", "Trigger rate;t;Hz", 100, 0, 100);
        for (int i = 0; i < 500; i++) h2.Fill(rng.Uniform(0,100));
        h2.Write();
    });

    // 6. multi_tree.root — multiple TTrees + histograms in same file
    generate(base + "/multi_tree.root", [&](TFile* f) {
        TH1F h("summary", "Summary;x;n", 50, 0, 50);
        for (int i = 0; i < 200; i++) h.Fill(rng.Uniform(0,50));
        h.Write();
        TTree t1("electrons", "Electron tree");
        float pt1; t1.Branch("pt", &pt1, "pt/F");
        for (int i = 0; i < 200; i++) { pt1 = rng.Gaus(40, 5); t1.Fill(); }
        t1.Write();
        TTree t2("muons", "Muon tree");
        float pt2; t2.Branch("pt", &pt2, "pt/F");
        for (int i = 0; i < 200; i++) { pt2 = rng.Gaus(20, 3); t2.Fill(); }
        t2.Write();
    });

    // 7. uncompressed.root — no compression
    generate(base + "/uncompressed.root", [&](TFile* f) {
        f->SetCompressionLevel(0);
        TH1F h("h", "Uncompressed;x;n", 50, -3, 3);
        for (int i = 0; i < 500; i++) h.Fill(rng.Gaus(0,1));
        h.Write();
    });

    // 8. profile.root — TProfile
    generate(base + "/profile.root", [&](TFile* f) {
        TProfile p("prof", "Profile;x;mean y", 50, -5, 5);
        for (int i = 0; i < 1000; i++) p.Fill(rng.Gaus(0,2), rng.Gaus(0,1));
        p.Write();
    });

    // 9. tgraph.root — TGraph
    generate(base + "/tgraph.root", [&](TFile* f) {
        const int n = 20;
        double x[n], y[n];
        for (int i = 0; i < n; i++) { x[i] = i; y[i] = rng.Gaus(i, 0.5); }
        TGraph g(n, x, y);
        g.SetName("graph"); g.SetTitle("Simple graph;x;y");
        g.Write();
    });

    // 10. ntuple.root — TNtuple (simplest possible columnar data)
    generate(base + "/ntuple.root", [&](TFile* f) {
        TNtuple nt("ntuple", "Simple ntuple", "x:y:z:t");
        for (int i = 0; i < 500; i++)
            nt.Fill(rng.Gaus(0,1), rng.Gaus(0,1), rng.Gaus(0,1), rng.Uniform(0,100));
        nt.Write();
    });

    printf("\nDone. %d fixture files written to %s\n", 10, outdir);
}